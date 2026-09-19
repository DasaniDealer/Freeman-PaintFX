package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class CanvasFeature {

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
