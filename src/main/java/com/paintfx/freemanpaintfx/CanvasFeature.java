package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class CanvasFeature {
    //Mouse Free Draw
    static void drawLine(GraphicsContext gc, Canvas canvas) {
        canvas.setOnMouseDragged(event -> {
            double x = event.getX();
            double y = event.getY();

            gc.lineTo(x, y);
            gc.stroke();
        });

        canvas.setOnMousePressed(event -> {
            gc.beginPath();
            gc.moveTo(event.getX(), event.getY());
            gc.stroke();
        });
    }

    //Straight Line Draw
    static double startX, startY;

    static void drawStraight(GraphicsContext gc, Canvas canvas) {
        GraphicsContext pgc = canvas.getGraphicsContext2D();

        canvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();
        });

        canvas.setOnMouseDragged(event -> {
            //Clear Preview Canvas
            pgc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            pgc.setStroke(gc.getStroke());
            pgc.setLineWidth(gc.getLineWidth());
            pgc.setLineCap(gc.getLineCap());
            pgc.setLineJoin(gc.getLineJoin());

            pgc.strokeLine(startX, startY, event.getX(), event.getY());
        });

        canvas.setOnMouseReleased(event -> {
            pgc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.strokeLine(startX, startY, event.getX(), event.getY());
        });
    }

    static void drawDashed(GraphicsContext gc, Canvas canvas, double lineWidth) {
        canvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();
        });

        canvas.setOnMouseReleased(event -> {
            gc.setLineWidth(lineWidth);

            gc.setLineDashes(3 * lineWidth, 2 * lineWidth);

            gc.beginPath();
            gc.moveTo(startX, startY);
            gc.lineTo(event.getX(), event.getY());
            gc.stroke();

            gc.setLineDashes((double[]) null);
        });
    }

    //Canvas Resizing
    static void canvasResize(Canvas canvas, GraphicsContext gc, double newWidth, double newHeight) {
        double oldWidth = canvas.getWidth();
        double oldHeight = canvas.getHeight();

        if (newWidth <= 0 || newHeight <= 0 || (newWidth == oldWidth && newHeight == oldHeight)) {
            return;
        }
        //Backup Of Settings
        javafx.scene.paint.Paint currentStroke = gc.getStroke();
        double currentWidth = gc.getLineWidth();
        StrokeLineCap currentCap = gc.getLineCap();
        StrokeLineJoin currentJoin = gc.getLineJoin();

        WritableImage buffer = new WritableImage((int) oldWidth, (int) oldHeight);
        canvas.snapshot(null, buffer);
        gc.clearRect(0, 0, newWidth, newHeight);

        gc.setStroke(currentStroke);
        gc.setLineWidth(currentWidth);
        gc.setLineCap(currentCap);
        gc.setLineJoin(currentJoin);

        gc.drawImage(buffer, 0, 0);
    }
}
