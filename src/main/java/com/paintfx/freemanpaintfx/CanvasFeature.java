package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class CanvasFeature {
    static double startX, startY;
    //Object Initialization
    static Path path;
    static Line line;

    //Mouse Free Draw
    static void drawLine(Pane canvas, DrawSettings drawSettings) {
        canvas.setOnMousePressed(event -> {
            path = new Path();
            //Path Settings
            path.setStroke(drawSettings.getColor());
            path.setStrokeWidth(drawSettings.getSize());
            path.setStrokeLineCap(StrokeLineCap.ROUND);
            path.setStrokeLineJoin(StrokeLineJoin.ROUND);

            path.getElements().add(new MoveTo(event.getX(), event.getY()));

            canvas.getChildren().add(path);
        });
        canvas.setOnMouseDragged(event -> {
            if (path == null) {return;}

            path.getElements().add(new LineTo(event.getX(), event.getY()));
        });

        canvas.setOnMouseReleased(event -> {path = null;});
    }

    //Straight Line Draw
    static void drawStraight(Pane canvas, DrawSettings drawSettings, Boolean dashed) {

        canvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();

            line = new Line(startX,startY,startX,startY);

            line.setStroke(drawSettings.getColor());
            line.setStrokeWidth(drawSettings.getSize());
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            //Dash Boolean
            if (dashed) {
                double lineWidth = drawSettings.getSize();
                line.getStrokeDashArray().addAll(3 * lineWidth, 2 * lineWidth);
            }

            canvas.getChildren().add(line);
        });

        canvas.setOnMouseDragged(event -> {
            if (line == null) {return;}

            line.setEndX(event.getX());
            line.setEndY(event.getY());
        });

        canvas.setOnMouseReleased(event -> {line = null;});
    }

    static void grabColor(Pane canvas, DrawSettings drawSettings, ToggleGroup toolToggle) {
        canvas.setCursor(javafx.scene.Cursor.CROSSHAIR);

        canvas.setOnMouseClicked(event -> {
            //Pick Colour
            double x = event.getScreenX();
            double y = event.getScreenY();
            Color pickedColor = drawSettings.getColorGrab().getPixelColor(x, y);

            drawSettings.getColorPicker().setValue(pickedColor);

            //Untoggle
            canvas.setCursor(javafx.scene.Cursor.DEFAULT);
            canvas.setOnMouseClicked(null);

            toolToggle.selectToggle(null);
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
