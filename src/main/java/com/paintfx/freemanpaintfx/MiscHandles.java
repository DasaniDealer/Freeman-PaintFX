package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class MiscHandles {
    //Apply Brush Settings
    static void applySettings(GraphicsContext gc, DrawSettings drawSettings, boolean dashed) {
        gc.setStroke(drawSettings.getColor());
        gc.setFill(drawSettings.getColor());
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

        SelectHandles.commitPaste(context.mainCanvas(), context.prevCanvas());
    }
}
