package com.paintfx.freemanpaintfx;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class ShapeFeature {
    private static double startX;
    private static double startY;

    private static Rectangle rectangle;
    private static Polygon triangle;
    private static Circle circle;
    private static Ellipse ellipse;

    public static void rectDraw(Pane canvas, ColorPicker colorPicker, Slider sizeSlider,
                                Boolean filled, Boolean dashed, Boolean square) {
        canvas.setOnMousePressed( event -> {
            startX = event.getX();
            startY = event.getY();

            rectangle = new Rectangle();
            rectangle.setX(startX);
            rectangle.setY(startY);

            if(filled) {rectangle.setFill(colorPicker.getValue());}
            else {rectangle.setFill(Color.TRANSPARENT);}

            rectangle.setStroke(colorPicker.getValue());
            rectangle.setStrokeWidth(sizeSlider.getValue());

            if (dashed) {
                double lineWidth = sizeSlider.getValue();
                rectangle.getStrokeDashArray().addAll(3 * lineWidth, 2 * lineWidth);
            }

            canvas.getChildren().add(rectangle);
        });

        canvas.setOnMouseDragged(event -> {
            if (rectangle == null) return;

            double currentX = event.getX();
            double currentY = event.getY();

            double width = Math.abs(startX - currentX);
            double height = Math.abs(startY - currentY);

            if(square) {
                double side = Math.max(width, height);
                width = side;
                height = side;

                double x = (currentX < startX) ? startX - side : startX;
                double y = (currentY < startY) ? startY - side : startY;

                rectangle.setX(x);
                rectangle.setY(y);
            }
            else {
                rectangle.setX(Math.min(startX, currentX));
                rectangle.setY(Math.min(startY, currentY));
            }

            rectangle.setWidth(width);
            rectangle.setHeight(height);
        });

        canvas.setOnMouseReleased(event -> {rectangle = null;});
    }

    public static void triangleDraw(Pane canvas, ColorPicker colorPicker, Slider sizeSlider,
                                    Boolean filled, Boolean dashed) {
        canvas.setOnMousePressed( event -> {
            startX = event.getX();
            startY = event.getY();

            triangle = new Polygon();

            if(filled) {triangle.setFill(colorPicker.getValue());}
            else {triangle.setFill(Color.TRANSPARENT);}

            triangle.setStroke(colorPicker.getValue());
            triangle.setStrokeWidth(sizeSlider.getValue());

            if (dashed) {
                double lineWidth = sizeSlider.getValue();
                triangle.getStrokeDashArray().addAll(3 * lineWidth, 2 * lineWidth);
            }

            canvas.getChildren().add(triangle);
        });

        canvas.setOnMouseDragged(event -> {
            if (triangle == null) return;

            double currentX = event.getX();
            double currentY = event.getY();

            double firstPointX = startX + (currentX - startX) / 2;
            double firstPointY = startY;

            double secondPointX = startX;
            double secondPointY = currentY;

            double thirdPointX = currentX;
            double thirdPointY  = currentY;

           triangle.getPoints().setAll(
                firstPointX, firstPointY,
                secondPointX, secondPointY,
                thirdPointX, thirdPointY
           );
        });

        canvas.setOnMouseReleased(event -> {triangle = null;});
    }

    public static void circleDraw(Pane canvas, ColorPicker colorPicker, Slider sizeSlider,
                                  Boolean filled, Boolean dashed) {
        canvas.setOnMousePressed( event -> {
            startX = event.getX();
            startY = event.getY();

            circle = new Circle(startX, startY, 0);

            if(filled) {circle.setFill(colorPicker.getValue());}
            else {circle.setFill(Color.TRANSPARENT);}

            circle.setStroke(colorPicker.getValue());
            circle.setStrokeWidth(sizeSlider.getValue());

            if (dashed) {
                double lineWidth = sizeSlider.getValue();
                circle.getStrokeDashArray().addAll(3 * lineWidth, 2 * lineWidth);
            }

            canvas.getChildren().add(circle);
        });

        canvas.setOnMouseDragged(event -> {
            if (circle == null) return;

            double distanceX = event.getX() - startX;
            double distanceY = event.getY() - startY;

            double radius = Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));
            circle.setRadius(radius);
        });

        canvas.setOnMouseReleased(event -> {circle = null;});
    }

    public static void ellipseDraw(Pane canvas, ColorPicker colorPicker, Slider sizeSlider,
                                   Boolean filled, Boolean dashed) {
        canvas.setOnMousePressed( event -> {
            startX = event.getX();
            startY = event.getY();

            ellipse = new Ellipse(startX, startY, 0, 0);

            if(filled) {ellipse.setFill(colorPicker.getValue());}
            else {ellipse.setFill(Color.TRANSPARENT);}

            ellipse.setStroke(colorPicker.getValue());
            ellipse.setStrokeWidth(sizeSlider.getValue());

            if (dashed) {
                double lineWidth = sizeSlider.getValue();
                ellipse.getStrokeDashArray().addAll(3 * lineWidth, 2 * lineWidth);
            }

            canvas.getChildren().add(ellipse);
        });

        canvas.setOnMouseDragged(event -> {
            if (ellipse == null) return;

            else {
                double currentX = event.getX();
                double currentY = event.getY();

                double radiusX = Math.abs(currentX - startX) / 2.0;
                double radiusY = Math.abs(currentY - startY) / 2.0;

                double centerX = startX + (currentX >= startX ? radiusX : -radiusX);
                double centerY = startY + (currentY >= startY ? radiusY : -radiusY);

                ellipse.setCenterX(centerX);
                ellipse.setCenterY(centerY);

                ellipse.setRadiusX(radiusX);
                ellipse.setRadiusY(radiusY);
            }
        });

        canvas.setOnMouseReleased(event -> ellipse = null);
    }
}




