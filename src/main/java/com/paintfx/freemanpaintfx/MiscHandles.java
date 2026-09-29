package com.paintfx.freemanpaintfx;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class MiscHandles {
    //Apply Brush Settings
    static void applySettings(GraphicsContext gc, DrawSettings drawSettings, boolean dashed) {
        gc.setStroke(drawSettings.getColor());
        gc.setLineWidth(drawSettings.getSize());
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        if (dashed) {
            double lineWidth = drawSettings.getSize();
            gc.setLineDashes(3 * lineWidth, 2 * lineWidth);
        } else {
            gc.setLineDashes((double[]) null);
        }
    }

    //Clear Active Mouse Listeners
    static void clearListeners(TabFeature.TabRecord context) {
        var container = context.canvasContainer();
        var prevCanvas = context.prevCanvas();

        container.setOnMousePressed(null);
        container.setOnMouseDragged(null);
        container.setOnMouseReleased(null);

        prevCanvas.setOnMousePressed(null);
        prevCanvas.setOnMouseDragged(null);
        prevCanvas.setOnMouseReleased(null);
    }

    //Canvas Resizing
    public static void canvasResize(Canvas canvas, double newWidth, double newHeight) {
        double oldWidth = canvas.getWidth();
        double oldHeight = canvas.getHeight();

        if (newWidth <= 0 || newHeight <= 0 || (newWidth == oldWidth && newHeight == oldHeight)) {
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();

        int bufWidth = Math.max(1, (int) oldWidth);
        int bufHeight = Math.max(1, (int) oldHeight);

        WritableImage buffer = new WritableImage(bufWidth, bufHeight);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        canvas.snapshot(params, buffer);

        gc.save();
        gc.clearRect(0, 0, newWidth, newHeight);
        gc.drawImage(buffer, 0, 0);
        gc.restore();
    }
}
