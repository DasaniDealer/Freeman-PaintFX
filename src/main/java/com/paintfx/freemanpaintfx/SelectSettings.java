package com.paintfx.freemanpaintfx;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class SelectSettings {
    private static double startX, startY;
    private static double endX, endY;
    private static double dragX, dragY;
    private static boolean hasSelection = false;
    private static boolean isDraggingSelection = false;

    //Context Menu
    private static ContextMenu activeMenu = null;
    private static WritableImage internalClipboard = null;
    private static boolean isPastingMode = false;
    private static double pasteX, pasteY;

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
            if (activeMenu != null) {
                activeMenu.hide();
                activeMenu = null;
            }

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
                    isPastingMode = false;
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

                double x = Math.floor(Math.min(startX, event.getX()));
                double y = Math.floor(Math.min(startY, event.getY()));
                double width = Math.floor(Math.abs(startX - event.getX()));
                double height = Math.floor(Math.abs(startY - event.getY()));

                pgc.strokeRect(x, y, width, height);

            } else if (isDraggingSelection && selectionSnapshot != null) {
                double dx = event.getX() - dragX;
                double dy = event.getY() - dragY;

                selectX = Math.round(selectX + dx);
                selectY = Math.round(selectY + dy);

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
                endX = event.getX();
                endY = event.getY();

                selectX = Math.floor(Math.min(startX, endX));
                selectY = Math.floor(Math.min(startY, endY));
                selectW = Math.floor(Math.abs(startX - endX));
                selectH = Math.floor(Math.abs(startY - endY));

                if (selectW > 1 && selectH > 1) {
                    //Snapshot Params
                    SnapshotParameters params = new SnapshotParameters();
                    params.setFill(Color.TRANSPARENT);
                    Rectangle2D rect = new Rectangle2D(selectX, selectY, selectW, selectH);
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
                hasSelection = false;
            } else if (isDraggingSelection) {
                isDraggingSelection = false;
            }
        });

        //Pop-Up Menu
        prevCanvas.setOnContextMenuRequested(menuEvent -> {
            if (selectionSnapshot == null && selectW <= 1) return;

            ContextMenu popupMenu = new ContextMenu();
            popupMenu.setAutoHide(true);
            activeMenu = popupMenu;

            MenuItem cutItem = new MenuItem("Cut");
            MenuItem copyItem = new MenuItem("Copy");
            MenuItem pasteItem = new MenuItem("Paste");
            MenuItem cancelItem = new MenuItem("Cancel");

            if (internalClipboard == null) {
                pasteItem.setDisable(true);
            }

            cutItem.setOnAction(e -> {
                CanvasHistory.saveState(mainCanvas);

                internalClipboard = selectionSnapshot;

                selectionSnapshot = null;
                pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                activeMenu = null;
                PaintLogger.getInstance().logHandle("SelectSettings","Image Cut");
            });

            copyItem.setOnAction(e -> {
                internalClipboard = selectionSnapshot;

                if (selectionSnapshot != null) {
                    gc.drawImage(selectionSnapshot, selectX, selectY);
                }
                pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                selectionSnapshot = null;
                activeMenu = null;
                PaintLogger.getInstance().logHandle("SelectSettings","Image Copied");
            });

            pasteItem.setOnAction(e -> {
                pasteFromClipboard(prevCanvas,  Math.round(menuEvent.getX()), Math.round(menuEvent.getY()));
                activeMenu = null;
                PaintLogger.getInstance().logHandle("SelectSettings","Image Pasted");
            });

            cancelItem.setOnAction(e -> {
                if (selectionSnapshot != null) {
                    gc.drawImage(selectionSnapshot, selectX, selectY);
                    selectionSnapshot = null;
                }
                pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                activeMenu = null;
            });

            popupMenu.getItems().addAll(cutItem, copyItem, pasteItem, cancelItem);
            popupMenu.show(prevCanvas, menuEvent.getScreenX(), menuEvent.getScreenY());
        });
    }

    private static void pasteFromClipboard(Canvas prevCanvas, double x, double y) {
        if (internalClipboard == null) {return;}

        isPastingMode = true;
        hasSelection = false;
        pasteX = x;
        pasteY = y;

        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();
        pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
        pgc.drawImage(internalClipboard, pasteX, pasteY);

        //Border Guide
        pgc.setStroke(Color.GRAY);
        pgc.setLineWidth(1.0);
        pgc.setLineDashes(2.0);
        pgc.strokeRect(pasteX, pasteY, internalClipboard.getWidth(), internalClipboard.getHeight());

        //Transfer Variables
        selectionSnapshot = internalClipboard;
        selectX = Math.round(pasteX);
        selectY = Math.round(pasteY);
        selectW = Math.round(internalClipboard.getWidth());
        selectH = Math.round(internalClipboard.getHeight());
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