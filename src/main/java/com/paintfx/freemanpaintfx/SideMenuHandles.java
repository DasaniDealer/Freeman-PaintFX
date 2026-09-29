package com.paintfx.freemanpaintfx;

import javafx.event.ActionEvent;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Optional;

/**
 * Handles Canvas interactions, including
 * free draw, line draw, color picking, and canvas resizing
 * <p>
 */
public class SideMenuHandles {
    static double startX, startY;
    static Boolean isDrawing = true;

    //Mouse Free Draw
    static void drawLine(Pane canvas, DrawSettings drawSettings, GraphicsContext gc, Boolean dashed) {
        canvas.setOnMousePressed(event -> {
            gc.beginPath();
            gc.moveTo(event.getX(), event.getY());
            gc.stroke();
        });
        canvas.setOnMouseDragged(event -> {
            MiscHandles.applySettings(gc, drawSettings, dashed);
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
            if (!isDrawing) {
                return;
            }

            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
            MiscHandles.applySettings(pgc, drawSettings, dashed);
            pgc.strokeLine(startX, startY, event.getX(), event.getY());

        });

        prevCanvas.setOnMouseReleased(event -> {
            if (!isDrawing) {
                return;
            }
            isDrawing = false;

            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
            MiscHandles.applySettings(gc, drawSettings, dashed);
            gc.strokeLine(startX, startY, event.getX(), event.getY());
        });

    }

    static void eraserTool(Pane canvas, DrawSettings drawSettings, GraphicsContext gc, boolean dashed) {
        canvas.setOnMousePressed(event -> {
            clearPixelsAt(event.getX(), event.getY(), gc, drawSettings.getSize());
        });

        // Handle when the user drags the mouse to erase along a path
        canvas.setOnMouseDragged(event -> {
            MiscHandles.applySettings(gc, drawSettings, dashed);
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

        if (result.isPresent()) {
            if (result.get() == buttonConfirm) {
                canvas.getChildren().clear();
            } else {
                event.consume();
            }
        }
    }
}