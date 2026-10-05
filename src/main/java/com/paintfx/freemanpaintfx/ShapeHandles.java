package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;

public class ShapeHandles {
    private static double startX;
    private static double startY;

    //Square & Rectangle Function
    public static void rectDraw(Canvas mainCanvas, Canvas prevCanvas, DrawSettings drawSettings,
                                boolean filled, boolean dashed) {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

        prevCanvas.setOnMousePressed( event -> {
            startX = event.getX();
            startY = event.getY();
        });

        prevCanvas.setOnMouseDragged(event -> {
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());

            double x, y, width, height;
            x = Math.min(startX, event.getX());
            y = Math.min(startY, event.getY());
            width = Math.abs(startX - event.getX());
            height = Math.abs(startY - event.getY());

            MiscHandles.applySettings(pgc, drawSettings, dashed);
            if (filled) {pgc.fillRect(x, y, width, height);}

            pgc.strokeRect(x, y, width, height);
        });

        prevCanvas.setOnMouseReleased(event -> {
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());

            double x, y, width, height;
            x = Math.min(startX, event.getX());
            y = Math.min(startY, event.getY());
            width = Math.abs(startX - event.getX());
            height = Math.abs(startY - event.getY());

            MiscHandles.applySettings(gc, drawSettings, dashed);
            if (filled) {gc.fillRect(x, y, width, height);}

            gc.strokeRect(x, y, width, height);
        });
    }

    public static void polygonDraw(Canvas mainCanvas, Canvas prevCanvas, DrawSettings drawSettings,
                                   boolean filled, boolean dashed, int sides) {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

        prevCanvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();
        });

        prevCanvas.setOnMouseDragged(event -> {
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
            MiscHandles.applySettings(pgc, drawSettings, dashed);
            polygonRender(pgc, startX, startY, event.getX(), event.getY(), sides, filled);
        });

        prevCanvas.setOnMouseReleased(event -> {
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
            MiscHandles.applySettings(gc, drawSettings, dashed);
            polygonRender(gc, startX, startY, event.getX(), event.getY(), sides, filled);
        });
    }
    //Circle Function
    public static void circleDraw(Canvas mainCanvas, Canvas prevCanvas, DrawSettings drawSettings,
                                  boolean filled, boolean dashed) {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

        prevCanvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();
        });

        prevCanvas.setOnMouseDragged(event -> {
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
            MiscHandles.applySettings(pgc, drawSettings, dashed);

            double radius = Math.hypot(event.getX() - startX, event.getY() - startY);
            double x = startX - radius;
            double y = startY - radius;
            double diameter = radius * 2;

            if (filled) { pgc.fillOval(x, y, diameter, diameter); }
            pgc.strokeOval(x, y, diameter, diameter);
        });

        prevCanvas.setOnMouseReleased(event -> {
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
            MiscHandles.applySettings(gc, drawSettings, dashed);

            double radius = Math.hypot(event.getX() - startX, event.getY() - startY);
            double x = startX - radius;
            double y = startY - radius;
            double diameter = radius * 2;

            if (filled) { gc.fillOval(x, y, diameter, diameter); }
            gc.strokeOval(x, y, diameter, diameter);
        });
    }
    //Ellipse Function
    public static void ellipseDraw(Canvas mainCanvas, Canvas prevCanvas, DrawSettings drawSettings,
                                   boolean filled, boolean dashed) {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

        prevCanvas.setOnMousePressed( event -> {
            startX = event.getX();
            startY = event.getY();
        });

        prevCanvas.setOnMouseDragged(event -> {
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
            MiscHandles.applySettings(pgc, drawSettings, dashed);

            double x = Math.min(startX, event.getX());
            double y = Math.min(startY, event.getY());
            double width = Math.abs(startX - event.getX());
            double height = Math.abs(startY - event.getY());

            if (filled) { pgc.fillOval(x, y, width, height); }
            pgc.strokeOval(x, y, width, height);
        });

        prevCanvas.setOnMouseReleased(event -> {
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
            MiscHandles.applySettings(gc, drawSettings, dashed);

            double x = Math.min(startX, event.getX());
            double y = Math.min(startY, event.getY());
            double width = Math.abs(startX - event.getX());
            double height = Math.abs(startY - event.getY());

            if (filled) { gc.fillOval(x, y, width, height); }
            gc.strokeOval(x, y, width, height);
        });
    }
    //Polygon Renderer
    private static void polygonRender(GraphicsContext context, double startX, double startY,
                                      double currentX, double currentY, int sides, boolean isFilled) {

        double dx = currentX - startX;
        double dy = currentY - startY;
        double radius = Math.sqrt(dx * dx + dy * dy);

        if (radius < 1) return;

        double[] xPoints = new double[sides];
        double[] yPoints = new double[sides];

        double startAngle = Math.atan2(dy, dx);

        for (int i = 0; i < sides; i++) {
            double angle = startAngle + (i * 2 * Math.PI / sides);
            xPoints[i] = startX + radius * Math.cos(angle);
            yPoints[i] = startY + radius * Math.sin(angle);
        }

        if (isFilled) {
            context.fillPolygon(xPoints, yPoints, sides);
        }
        context.strokePolygon(xPoints, yPoints, sides);
    }
}