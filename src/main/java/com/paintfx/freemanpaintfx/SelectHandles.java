package com.paintfx.freemanpaintfx;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;

public class SelectHandles {
    private static double startX, startY;
    private static double selectX, selectY, selectWidth, selectHeight;
    private static WritableImage croppedSegment = null;
    private static boolean isMovingMode = false;

    private static ContextMenu copyPasteMenu = null;

    public static void SelectionTool(Canvas mainCanvas, Canvas prevCanvas) {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

        hideFloatingMenu();

        prevCanvas.setOnMousePressed(event -> {
            double clickX = event.getX();
            double clickY = event.getY();

            if (croppedSegment != null &&
                    clickX >= selectX && clickX <= (selectX + selectWidth) &&
                    clickY >= selectY && clickY <= (selectY + selectHeight)) {
                isMovingMode = true;
                startX = clickX;
                startY = clickY;
                hideFloatingMenu();
            } else {
                isMovingMode = false;
                startX = clickX;
                startY = clickY;
                croppedSegment = null;
                pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                hideFloatingMenu();
            }
        });

        prevCanvas.setOnMouseDragged(event -> {
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());

            if (isMovingMode) {
                double deltaX = event.getX() - startX;
                double deltaY = event.getY() - startY;
                double liveX = selectX + deltaX;
                double liveY = selectY + deltaY;

                pgc.drawImage(croppedSegment, liveX, liveY);

                pgc.setStroke(Color.BLUE);
                pgc.setLineWidth(1);
                pgc.setLineDashes(5, 5);
                pgc.strokeRect(liveX, liveY, selectWidth, selectHeight);
            } else {
                selectX = Math.min(startX, event.getX());
                selectY = Math.min(startY, event.getY());
                selectWidth = Math.abs(startX - event.getX());
                selectHeight = Math.abs(startY - event.getY());

                if (selectWidth < 1 || selectHeight < 1) return;

                pgc.setStroke(Color.BLUE);
                pgc.setLineWidth(1);
                pgc.setLineDashes(5, 5);
                pgc.strokeRect(selectX, selectY, selectWidth, selectHeight);
            }
        });

        prevCanvas.setOnMouseReleased(event -> {
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());

            if (isMovingMode) {
                double deltaX = event.getX() - startX;
                double deltaY = event.getY() - startY;
                selectX += deltaX;
                selectY += deltaY;

                gc.drawImage(croppedSegment, selectX, selectY);

                pgc.setStroke(Color.DARKBLUE);
                pgc.setLineDashes(3, 3);
                pgc.strokeRect(selectX, selectY, selectWidth, selectHeight);

                showMenuAtSelection(prevCanvas);
            } else {
                if (selectWidth < 2 || selectHeight < 2) return;

                croppedSegment = new WritableImage((int) selectWidth, (int) selectHeight);

                javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
                params.setViewport(new Rectangle2D(selectX, selectY, selectWidth, selectHeight));
                mainCanvas.snapshot(params, croppedSegment);

                pgc.setStroke(Color.DARKBLUE);
                pgc.setLineDashes(3, 3);
                pgc.strokeRect(selectX, selectY, selectWidth, selectHeight);

                showMenuAtSelection(prevCanvas);
            }
        });
    }

    private static void showMenuAtSelection(Canvas canvas) {
        hideFloatingMenu(); // Guard loop reset

        copyPasteMenu = new ContextMenu();
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");

        copyItem.setOnAction(event -> copySelectedToClipboard());

        pasteItem.setOnAction(event -> {
            Canvas mainCanvas = (Canvas) canvas.getScene().lookup("#mainCanvas"); // Or passed down context references
            // Call paste function
            pasteClipboardContent(canvas, canvas);
        });

        copyPasteMenu.getItems().addAll(copyItem, pasteItem);

        // Turn screen workspace canvas location vectors into absolute window coordinates
        javafx.geometry.Point2D windowAnchor = canvas.localToScreen(selectX + selectWidth, selectY);

        if (windowAnchor != null) {
            copyPasteMenu.show(canvas, windowAnchor.getX() + 5, windowAnchor.getY());
        }
    }

    public static void hideFloatingMenu() {
        if (copyPasteMenu != null) {
            copyPasteMenu.hide();
            copyPasteMenu = null;
        }
    }

    public static void copySelectedToClipboard() {
        if (croppedSegment == null) return;
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putImage(croppedSegment);
        clipboard.setContent(content);
        hideFloatingMenu();
    }

    public static void pasteClipboardContent(Canvas mainCanvas, Canvas prevCanvas) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasImage()) {
            Image clipboardImage = clipboard.getImage();

            selectX = 40;
            selectY = 40;
            selectWidth = clipboardImage.getWidth();
            selectHeight = clipboardImage.getHeight();

            croppedSegment = new WritableImage(clipboardImage.getPixelReader(), (int) selectWidth, (int) selectHeight);

            GraphicsContext pgc = prevCanvas.getGraphicsContext2D();
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
            pgc.drawImage(croppedSegment, selectX, selectY);
            pgc.setStroke(Color.DARKBLUE);
            pgc.setLineDashes(3, 3);
            pgc.strokeRect(selectX, selectY, selectWidth, selectHeight);

            showMenuAtSelection(prevCanvas);
        }
    }
}