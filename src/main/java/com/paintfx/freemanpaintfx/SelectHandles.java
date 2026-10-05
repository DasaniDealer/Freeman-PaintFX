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

public class SelectHandles {
    private static double startX;
    private static double startY;
    private static boolean hasSelection = false;
    private static ContextMenu activeMenu = null;

    //Paste Variables
    static boolean isPastingMode = false;
    private static double pasteX;
    private static double pasteY;
    private static double dragOffsetX;
    private static double dragOffsetY;

    private static Image internalClipboard = null;

    static void selectionTool(Canvas mainCanvas, Canvas prevCanvas) {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

        final double[] bounds = new double[4];

        prevCanvas.setOnMousePressed(event -> {
            //After Paste Handle
            if (isPastingMode) {

                if (event.getX() >= pasteX && event.getX() <= (pasteX + internalClipboard.getWidth()) &&
                        event.getY() >= pasteY && event.getY() <= (pasteY + internalClipboard.getHeight())) {

                    dragOffsetX = event.getX() - pasteX;
                    dragOffsetY = event.getY() - pasteY;
                    return;
                } else {
                    commitPaste(mainCanvas, prevCanvas);
                    return;
                }
            }
            if (activeMenu != null && activeMenu.isShowing()) {
                activeMenu.hide();
                activeMenu = null;
            }

            if (hasSelection = false) {
                double selX = bounds[0];
                double selY = bounds[1];
                double selW = bounds[2];
                double selH = bounds[3];

                //If Click Outside Select Area
                if (event.getX() < selX || event.getX() > (selX + selW) ||
                        event.getY() < selY || event.getY() > (selY + selH)) {

                    pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                    hasSelection = false;
                    return;
                }
            }

            startX = event.getX();
            startY = event.getY();
            isDrawing = true;
        });

        prevCanvas.setOnMouseDragged(event -> {
            // Clear temporary canvas
            if (isPastingMode && internalClipboard != null) {
                pasteX = event.getX() - dragOffsetX;
                pasteY = event.getY() - dragOffsetY;

                // Redraw live preview
                pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                pgc.drawImage(internalClipboard, pasteX, pasteY);

                // Optional: Draw a subtle bounding box around the moving image
                pgc.setStroke(Color.GRAY);
                pgc.setLineWidth(1.0);
                pgc.setLineDashes(2.0);
                pgc.strokeRect(pasteX, pasteY, internalClipboard.getWidth(), internalClipboard.getHeight());
                return;
            }

            if (!isDrawing) return;

            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
            pgc.setStroke(Color.BLUE);
            pgc.setLineWidth(1.0);
            pgc.setLineDashes(4.0);

            double x = Math.min(startX, event.getX());
            double y = Math.min(startY, event.getY());
            double width = Math.abs(event.getX() - startX);
            double height = Math.abs(event.getY() - startY);

            pgc.strokeRect(x, y, width, height);
        });

        prevCanvas.setOnMouseReleased(event -> {
            if (isPastingMode) return;
            if (!isDrawing) return;
            isDrawing = false;

            //Dashed Rectangle
            double x = Math.min(startX, event.getX());
            double y = Math.min(startY, event.getY());
            double width = Math.abs(event.getX() - startX);
            double height = Math.abs(event.getY() - startY);

            if (width < 3 || height < 3) {
                pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                hasSelection = false;
                return;
            }

            bounds[0] = x;
            bounds[1] = y;
            bounds[2] = width;
            bounds[3] = height;
            hasSelection = true;

            //Pop-Up Menu
            ContextMenu popupMenu = new ContextMenu();
            popupMenu.setAutoHide(true);
            activeMenu = popupMenu;

            MenuItem copyItem = new MenuItem("Copy");
            MenuItem pasteItem = new MenuItem("Paste");
            MenuItem cancelItem = new MenuItem("Cancel");

            if (internalClipboard == null) {
                pasteItem.setDisable(true);
            }

            //Copy to Clipboard
            copyItem.setOnAction(e -> {
                copyToClipboard(mainCanvas, x, y, width, height);
                pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                hasSelection = false;
                activeMenu = null;
            });

            //Paste
            pasteItem.setOnAction(e -> {
                pasteFromClipboard(prevCanvas, x, y);
                activeMenu = null;
            });

            //Cancel
            cancelItem.setOnAction(e -> {
                pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                hasSelection = false;
                activeMenu = null;
            });

            popupMenu.setOnHidden(e -> {
                if (activeMenu != null) {
                    pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
                    hasSelection = false;
                }
            });

            popupMenu.getItems().addAll(copyItem, pasteItem, cancelItem);
            popupMenu.show(prevCanvas, event.getScreenX(), event.getScreenY());
        });
    }

    private static void copyToClipboard(Canvas mainCanvas, double x, double y, double width, double height) {
        try {
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            params.setViewport(new Rectangle2D(x, y, width, height));

            WritableImage croppedImage = new WritableImage((int) width, (int) height);
            internalClipboard = mainCanvas.snapshot(params, croppedImage);
        } catch (Exception ex) {
            System.err.println("Failed to copy image region: " + ex.getMessage());
        }
    }

    private static void pasteFromClipboard(Canvas prevCanvas, double x, double y) {
        if (internalClipboard == null) {
            System.out.println("Nothing to paste!");
            return;
        }

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
    }

    //Commit Paste
    public static void commitPaste(Canvas mainCanvas, Canvas prevCanvas) {
        if (!isPastingMode || internalClipboard == null) {return;}
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

        gc.drawImage(internalClipboard, pasteX, pasteY);

        pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());

        isPastingMode = false;
    }
}