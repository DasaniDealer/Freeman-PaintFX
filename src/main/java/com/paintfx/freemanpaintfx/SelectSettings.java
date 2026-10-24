package com.paintfx.freemanpaintfx;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static com.paintfx.freemanpaintfx.SideMenuHandles.isDrawing;

public class SelectSettings {
    private static double startX, startY;
    private static double endX, endY;
    private static double dragX, dragY;
    private static boolean hasSelection = false;
    private static boolean isDraggingSelection = false;

    //Snapshot Image
    static javafx.scene.image.WritableImage selectionSnapshot = null;
    static double selectX, selectY;
    static double selectW, selectH;

    static void selectionTool(Canvas mainCanvas, Canvas prevCanvas) {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

        gc.setImageSmoothing(false);
        pgc.setImageSmoothing(false);

        prevCanvas.setOnMousePressed(event -> {
            if (selectionSnapshot != null &&
                    event.getX() >= selectX && event.getX() <= (selectX + selectW) &&
                    event.getY() >= selectY && event.getY() <= (selectY + selectH)) {

                isDraggingSelection = true;
                hasSelection = false;
                dragX = event.getX();
                dragY = event.getY();
            } else {
                if (selectionSnapshot != null) {
                    gc.drawImage(selectionSnapshot, selectX, selectY);
                    selectionSnapshot = null;
                    pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                }

                startX = event.getX();
                startY = event.getY();
                hasSelection = true;
                isDraggingSelection = false;
            }
        });

        prevCanvas.setOnMouseDragged(event -> {
            if (hasSelection) {
                pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                //Visual Border
                pgc.setStroke(Color.BLUE);
                pgc.setLineWidth(1);
                pgc.setLineDashes(5.0);

                double x = Math.min(startX, event.getX());
                double y = Math.min(startY, event.getY());
                double weight = Math.abs(startX - event.getX());
                double height = Math.abs(startY - event.getY());

                pgc.strokeRect(x, y, weight, height);
            } else if (isDraggingSelection && selectionSnapshot != null) {
                double dx = event.getX() - dragX;
                double dy = event.getY() - dragY;

                selectX += dx;
                selectY += dy;

                dragX = event.getX();
                dragY = event.getY();
                //Re-render Snapshot Image
                pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                pgc.drawImage(selectionSnapshot, selectX, selectY);

                pgc.setStroke(Color.BLUE);
                pgc.setLineWidth(1);
                pgc.setLineDashes(5.0);
                pgc.strokeRect(selectX, selectY, selectW, selectH);
            }
        });

        prevCanvas.setOnMouseReleased(event -> {
            if (hasSelection) {
                hasSelection = false;
                endX = event.getX();
                endY = event.getY();

                selectX = Math.min(startX, endX);
                selectY = Math.min(startY, endY);
                selectW = Math.abs(startX - endX);
                selectH = Math.abs(startY - endY);

                if (selectW > 1 && selectH > 1) {
                    //Snapshot Params
                    javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
                    params.setFill(Color.TRANSPARENT);
                    javafx.geometry.Rectangle2D rect = new javafx.geometry.Rectangle2D(selectX, selectY, selectW, selectH);
                    params.setViewport(rect);

                    CanvasHistory.saveState(mainCanvas);
                    //Snap & Erase
                    selectionSnapshot = mainCanvas.snapshot(params, null);
                    gc.clearRect(selectX, selectY, selectW, selectH);
                    //Draw Temp Copy
                    pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                    pgc.drawImage(selectionSnapshot, selectX, selectY);
                    pgc.setStroke(Color.BLUE);
                    pgc.setLineWidth(1);
                    pgc.setLineDashes(5.0);
                    pgc.strokeRect(selectX, selectY, selectW, selectH);
                } else {
                    pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                    selectionSnapshot = null;
                }
            } else if (isDraggingSelection) {
                isDraggingSelection = false;
            }
        });


    }

    //Commit Paste
    public static void commitPaste(Canvas mainCanvas, Canvas prevCanvas) {
        if (selectionSnapshot != null) {
            GraphicsContext gc = mainCanvas.getGraphicsContext2D();
            GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

            //Commit
            gc.drawImage(selectionSnapshot, selectX, selectY);
            selectionSnapshot = null;
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
        }
    }
}