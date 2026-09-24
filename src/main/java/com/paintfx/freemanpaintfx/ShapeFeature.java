package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;

public class ShapeFeature {
    private static double startX;
    private static double startY;

    private static Rectangle rectangle;

    public static void rectDraw(Pane canvas, ColorPicker colorPicker, Slider sizeSlider, Boolean filled) {
        canvas.setOnMousePressed( event -> {
            startX = event.getX();
            startY = event.getY();

            rectangle = new Rectangle();
            rectangle.setX(startX);
            rectangle.setY(startY);

            if(filled) {
                rectangle.setFill(colorPicker.getValue());
            }
            else {
                rectangle.setFill(Color.TRANSPARENT);
            }
            rectangle.setStroke(colorPicker.getValue());
            rectangle.setStrokeWidth(sizeSlider.getValue());

            canvas.getChildren().add(rectangle);
        });

        canvas.setOnMouseDragged(event -> {
            if (rectangle == null) return;

            double currentX = event.getX();
            double currentY = event.getY();

            double x = Math.min(startX, currentX);
            double y = Math.min(startY, currentY);

            double width = Math.abs(startX - currentX);
            double height = Math.abs(startY - currentY);

            rectangle.setX(x);
            rectangle.setY(y);
            rectangle.setWidth(width);
            rectangle.setHeight(height);
        });

        canvas.setOnMouseReleased(event -> {
            rectangle = null;
        });
    }
}

