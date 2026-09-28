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
    static Boolean isDrawing = true;

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
    static void drawStraight(Canvas mainCanvas, Canvas prevCanvas, DrawSettings drawSettings, Boolean dashed) {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

        prevCanvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();
            isDrawing = true;
        });

        prevCanvas.setOnMouseDragged(event -> {
            if (!isDrawing) {return;}

            pgc.clearRect(0,0, prevCanvas.getWidth(), prevCanvas.getHeight());
            TabFeature.applySettings(pgc, drawSettings, dashed);
            pgc.strokeLine(startX, startY, event.getX(), event.getY());

        });

        prevCanvas.setOnMouseReleased(event -> {
            if(!isDrawing) {return;}
            isDrawing = false;

            pgc.clearRect(0,0, prevCanvas.getWidth(), prevCanvas.getHeight());
            TabFeature.applySettings(gc, drawSettings, dashed);
            gc.strokeLine(startX, startY, event.getX(), event.getY());
        });

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
    static void canvasResize(Canvas canvas, GraphicsContext gc, double oldWidth, double oldHeight) {
        double newWidth = canvas.getWidth();
        double newHeight = canvas.getHeight();

        if (newWidth <= 0 || newHeight <= 0 || (newWidth == oldWidth && newHeight == oldHeight)) {
            return;
        }
        //Backup Of Settings
        javafx.scene.paint.Paint currentStroke = gc.getStroke();
        double currentWidth = gc.getLineWidth();
        StrokeLineCap currentCap = gc.getLineCap();
        StrokeLineJoin currentJoin = gc.getLineJoin();
        double[] currentDashes = gc.getLineDashes();

        int bufWidth = Math.max(1, (int) oldWidth);
        int bufHeight = Math.max(1, (int) oldHeight);
        WritableImage buffer = new WritableImage(bufWidth, bufHeight);

        canvas.snapshot(null,buffer);

        gc.setStroke(currentStroke);
        gc.setLineWidth(currentWidth);
        gc.setLineCap(currentCap);
        gc.setLineJoin(currentJoin);
        gc.setLineDashes(currentDashes);

        gc.drawImage(buffer, 0, 0);
    }
}