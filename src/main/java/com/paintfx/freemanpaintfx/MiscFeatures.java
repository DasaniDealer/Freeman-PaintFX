package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class MiscFeatures {
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

        context.pgc().clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
        SelectSettings.commitPaste(context.mainCanvas(), context.prevCanvas());
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

    public static void starRender(GraphicsContext gc, double cx, double cy, int points, double rOuter, double rInner, boolean filled) {
        // Total vertices = outer points + inner valleys
        int totalVertices = points * 2;
        double[] xPoints = new double[totalVertices];
        double[] yPoints = new double[totalVertices];

        // Each step rotates by a fraction of 360 degrees (in radians)
        double angleStep = Math.PI / points;

        // Start pointing straight up (-90 degrees or -PI/2)
        double startAngle = -Math.PI / 2;

        for (int i = 0; i < totalVertices; i++) {
            // Alternate between outer radius and inner radius
            double currentRadius = (i % 2 == 0) ? rOuter : rInner;
            double currentAngle = startAngle + (i * angleStep);

            // Trigonometry to find vertex positions
            xPoints[i] = cx + Math.cos(currentAngle) * currentRadius;
            yPoints[i] = cy + Math.sin(currentAngle) * currentRadius;
        }

        if (filled) {
            gc.fillPolygon(xPoints, yPoints, totalVertices);
        }
        gc.strokePolygon(xPoints, yPoints, totalVertices);
    }
}
