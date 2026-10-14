package com.paintfx.freemanpaintfx;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class WebserverFeature {
    private HttpServer server;
    private final Map<String, Canvas> selectedCanvases = new ConcurrentHashMap<>();
    private static final int PORT = 8080;

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", this::handleRootRequest);
        server.createContext("/image", this::handleImageRequest);

        server.setExecutor(null);
        server.start();
        System.out.println("Webserver initialized at http://localhost:" + PORT);
    }

    public void stop() {
        if (server != null) server.stop(0);
    }

    public void updateSelection(Map<String, Canvas> newSelection) {
        selectedCanvases.clear();
        selectedCanvases.putAll(newSelection);
    }

    private void handleRootRequest(HttpExchange exchange) throws IOException {
        String responseHtml;

        if (selectedCanvases.isEmpty()) {
            responseHtml = "<html><body style='font-family:sans-serif; text-align:center; margin-top:50px; color:#555;'>"
                    + "<h2>Webserver Online</h2>"
                    + "<p>No canvases are currently selected for sharing. Please select tabs in the application.</p>"
                    + "</body></html>";
        } else {
            //Dynamic Tab Selection
            StringBuilder tabsHtml = new StringBuilder();
            StringBuilder contentHtml = new StringBuilder();
            int index = 0;

            for (String tabName : selectedCanvases.keySet()) {
                String tabId = "tab_" + index;
                tabsHtml.append(String.format("<button class='tablinks' onclick='openTab(event, \"%s\")'>%s</button>", tabId, tabName));
                contentHtml.append(String.format("<div id='%s' class='tabcontent' style='display:%s; text-align:center;'>"
                        + "<h3>%s</h3><img src='/image?name=%s' style='max-width:100%%; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'/>"
                        + "</div>", tabId, index == 0 ? "block" : "none", tabName, tabName));
                index++;
            }
            responseHtml = getHtmlTemplate(tabsHtml.toString(), contentHtml.toString());
        }

        byte[] bytes = responseHtml.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private void handleImageRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String tabName = (query != null && query.startsWith("name=")) ? query.substring(5) : "";
        Canvas canvas = selectedCanvases.get(tabName);

        if (canvas == null) {
            String error = "Canvas Not Shared";
            exchange.sendResponseHeaders(404, error.length());
            try (OutputStream os = exchange.getResponseBody()) { os.write(error.getBytes()); }
            return;
        }

        byte[] imageBytes = captureCanvasBytes(canvas);

        if (imageBytes != null) {
            exchange.getResponseHeaders().set("Content-Type", "image/png");
            exchange.sendResponseHeaders(200, imageBytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(imageBytes); }
        } else {
            exchange.sendResponseHeaders(500, 0);
        }
    }

    private byte[] captureCanvasBytes(Canvas canvas) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                WritableImage writableImage = canvas.snapshot(null, null);
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", baos);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();
            return baos.toByteArray();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private String getHtmlTemplate(String tabs, String content) {
        return "<html><head><style>"
                + "body {font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;}"
                + ".tab {overflow: hidden; border: 1px solid #ccc; background-color: #f11f1f1; border-radius: 4px 4px 0 0;}"
                + ".tab button {background-color: inherit; float: left; border: none; outline: none; cursor: pointer; padding: 14px 16px; transition: 0.3s; font-size: 16px;}"
                + ".tab button:hover {background-color: #ddd;}"
                + ".tab button.active {background-color: #fff; border-bottom: 2px solid #0078d7;}"
                + ".tabcontent {display: none; padding: 20px; border: 1px solid #ccc; border-top: none; background: #fff; border-radius: 0 0 4px 4px;}"
                + "</style><script>"
                + "function openTab(evt, tabName) {"
                + "  var i, tabcontent, tablinks;"
                + "  tabcontent = document.getElementsByClassName('tabcontent');"
                + "  for (i = 0; i < tabcontent.length; i++) { tabcontent[i].style.display = 'none'; }"
                + "  tablinks = document.getElementsByClassName('tablinks');"
                + "  for (i = 0; i < tablinks.length; i++) { tablinks[i].className = tablinks[i].className.replace(' active', ''); }"
                + "  document.getElementById(tabName).style.display = 'block';"
                + "  evt.currentTarget.className += ' active';"
                + "}"
                + "</script></head><body>"
                + "<h2>FreemanPaintFX Live Viewer</h2>"
                + "<div class='tab'>" + tabs + "</div>" + content
                + "</body></html>";
    }
}