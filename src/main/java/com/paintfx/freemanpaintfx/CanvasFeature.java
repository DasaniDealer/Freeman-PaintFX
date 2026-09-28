package com.paintfx.freemanpaintfx;

import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.Optional;

/**
 * Handles Canvas interactions, including
 * free draw, line draw, color picking, and canvas resizing
 * <p>
 */
public class CanvasFeature {
    static double startX, startY;
    //Object Initialization
    static Line line;

    //Mouse Free Draw
    static void drawLine(Pane canvas, DrawSettings drawSettings, GraphicsContext gc) {
        canvas.setOnMousePressed(event -> {
            gc.setStroke(drawSettings.getColor());
            gc.setLineWidth(drawSettings.getSize());
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.setLineJoin(StrokeLineJoin.ROUND);

            gc.beginPath();
            gc.moveTo(event.getX(), event.getY());
            gc.stroke();
        });
        canvas.setOnMouseDragged(event -> {
            gc.lineTo(event.getX(), event.getY());
            gc.stroke();
        });
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

    static void eraserTool(Pane canvas, DrawSettings drawSettings, GraphicsContext gc) {
        canvas.setOnMousePressed(event -> {
            clearPixelsAt(event.getX(), event.getY(), gc, drawSettings.getSize());
        });

        // Handle when the user drags the mouse to erase along a path
        canvas.setOnMouseDragged(event -> {
            clearPixelsAt(event.getX(), event.getY(), gc, drawSettings.getSize());
        });
    }

    private static void clearPixelsAt(double x, double y, GraphicsContext gc, double brushSize) {
        // Calculate Brush Size
        double offset = brushSize / 2;
        gc.clearRect(x - offset, y - offset, brushSize, brushSize);
    }

    //Color Grabber
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

    static void clearCanvas(Pane canvas, ActionEvent event) {
        Alert clearAlert = new Alert(Alert.AlertType.CONFIRMATION);
        clearAlert.setTitle("You Are About To Clear Canvas");
        clearAlert.setContentText("Are You Sure?");

        ButtonType buttonConfirm = new ButtonType("Confirm");
        ButtonType buttonCancel = new ButtonType("Cancel");

        clearAlert.getButtonTypes().setAll(buttonConfirm, buttonCancel);

        //Button Choice
        Optional<ButtonType> result = clearAlert.showAndWait();

        if(result.isPresent()) {
            if(result.get() == buttonConfirm) {
                canvas.getChildren().clear();
            }
            else {
                event.consume();
            }
        }
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