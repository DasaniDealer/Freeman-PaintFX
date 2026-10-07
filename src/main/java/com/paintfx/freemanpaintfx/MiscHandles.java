package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

        SelectSettings.commitPaste(context.mainCanvas(), context.prevCanvas());
    }

    public static void resizeCanvas(TabFeature.TabRecord record, double newWidth, double newHeight) {
        Canvas mainCanvas = record.mainCanvas();
        Canvas prevCanvas = record.prevCanvas();

        // 1. Prevent resizing to 0 or negative values
        if (newWidth <= 0 || newHeight <= 0) return;

        // 2. Snapshot existing drawings on the main canvas to preserve them
        javafx.scene.image.WritableImage snapshot = new javafx.scene.image.WritableImage(
                (int) mainCanvas.getWidth(),
                (int) mainCanvas.getHeight()
        );
        mainCanvas.snapshot(null, snapshot);

        // 3. Update dimensions of BOTH the main drawing and preview overlays
        mainCanvas.setWidth(newWidth);
        mainCanvas.setHeight(newHeight);

        prevCanvas.setWidth(newWidth);
        prevCanvas.setHeight(newHeight);

        // 4. Clear the new expanded area just in case, then redraw original artwork
        GraphicsContext gc = record.gc();
        gc.clearRect(0, 0, newWidth, newHeight);
        gc.drawImage(snapshot, 0, 0);
    }

    //Polygon Renderer
    static void polygonRender(GraphicsContext context, double startX, double startY,
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
