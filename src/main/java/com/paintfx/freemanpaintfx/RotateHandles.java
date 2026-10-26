package com.paintfx.freemanpaintfx;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static com.paintfx.freemanpaintfx.SelectSettings.*;

public class RotateHandles {
    //Handle Rotate
    public static void handleRotate(TabFeature.TabRecord record, double degrees) {
        Canvas mainCanvas = record.mainCanvas();
        Canvas prevCanvas = record.prevCanvas();
        GraphicsContext gc = record.gc();
        GraphicsContext pgc = record.pgc();

        if (selectionSnapshot != null) {
            selectionSnapshot = snapRotate(selectionSnapshot, degrees);

            double currentCenterX = selectX + (selectW / 2.0);
            double currentCenterY = selectY + (selectH / 2.0);

            double tempW = selectW;
            selectW = selectH;
            selectH = tempW;

            selectX = currentCenterX - (selectW / 2.0);
            selectY = currentCenterY - (selectH / 2.0);

            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
            pgc.drawImage(selectionSnapshot, selectX, selectY);

            pgc.setStroke(Color.BLUE);
            pgc.setLineWidth(1);
            pgc.setLineDashes(5.0);
            pgc.strokeRect(selectX, selectY, selectW, selectH);
        } else {
            CanvasHistory.saveState(mainCanvas);

            fullRotate(mainCanvas, gc, degrees);
            fullRotate(prevCanvas, pgc, degrees);
        }
    }

    //Rotate 90/180/270 Degrees
    static void fullRotate(Canvas canvas, GraphicsContext gc, double degrees) {
        double oldW = canvas.getWidth();
        double oldH = canvas.getHeight();
        double newCenterX = oldH / 2.0;
        double newCenterY = oldW / 2.0;
        double drawX = newCenterX - (oldW/ 2.0);
        double drawY = newCenterY - (oldH / 2.0);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage snapshot = canvas.snapshot(params,null);

        gc.clearRect(0,0,oldW,oldH);
        canvas.setWidth(oldH);
        canvas.setHeight(oldW);

        gc.save();
        gc.translate(newCenterX, newCenterY);
        gc.rotate(degrees);
        gc.translate(-newCenterX, -newCenterY);

        gc.drawImage(snapshot, drawX, drawY);
        gc.restore();
    }

    //Rotate Section
    private static WritableImage snapRotate(Image original, double degrees) {
        if (original == null) return null;

        double newWidth = original.getHeight();
        double newHeight = original.getWidth();

        Canvas scratchCanvas = new Canvas(newWidth, newHeight);
        GraphicsContext sgc = scratchCanvas.getGraphicsContext2D();
        sgc.setImageSmoothing(false);

        double centerX = newWidth / 2.0;
        double centerY = newHeight / 2.0;

        sgc.save();
        sgc.translate(centerX, centerY);
        sgc.rotate(degrees);
        sgc.translate(-centerX, -centerY);

        double drawX = centerX - (original.getWidth() / 2.0);
        double drawY = centerY - (original.getHeight() / 2.0);
        sgc.drawImage(original, drawX, drawY);
        sgc.restore();

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return scratchCanvas.snapshot(params, null);
    }

    //Handle Flip
    public static void handleFlip(TabFeature.TabRecord record,
                                  boolean hori, boolean vert) {
        Canvas mainCanvas = record.mainCanvas();
        Canvas prevCanvas = record.prevCanvas();
        GraphicsContext gc = record.gc();
        GraphicsContext pgc = record.pgc();

        if (selectionSnapshot != null) {
            selectionSnapshot = snapFlip(selectionSnapshot, hori, vert);

            pgc.clearRect(0,0,prevCanvas.getWidth(),prevCanvas.getHeight());
            pgc.drawImage(selectionSnapshot, selectX, selectY);

            pgc.setStroke(Color.BLUE);
            pgc.setLineWidth(1);
            pgc.setLineDashes(5.0);
            pgc.strokeRect(selectX, selectY, selectW, selectH);
        }
        else {
            CanvasHistory.saveState(mainCanvas);
            fullFlip(mainCanvas, gc, hori, vert);
            fullFlip(prevCanvas, pgc, hori, vert);
        }
    }

    //Flip Full Canvas
    private static void fullFlip(Canvas canvas, GraphicsContext gc,
                                 boolean hori, boolean vert) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage snapshot = canvas.snapshot(params, null);

        gc.clearRect(0,0,width,height);
        gc.save();

        double scaleX = hori ? -1 : 1;
        double scaleY = vert ? -1 : 1;
        double translateX = hori ? width : 0;
        double translateY = vert ? height : 0;

        gc.translate(translateX, translateY);
        gc.scale(scaleX, scaleY);
        gc.drawImage(snapshot, 0, 0);
        gc.restore();
    }

    //Flip Section
    private static WritableImage snapFlip(Image original,
                                          boolean hori, boolean vert) {
        if (original == null) return null;
        double width = original.getWidth();
        double height = original.getHeight();

        Canvas scratchCanvas = new Canvas(width, height);
        GraphicsContext sgc = scratchCanvas.getGraphicsContext2D();
        sgc.setImageSmoothing(false);

        sgc.save();
        double scaleX = hori ? -1 : 1;
        double scaleY = vert ? -1 : 1;
        double translateX = hori ? width : 0;
        double translateY = vert ? height : 0;

        sgc.translate(translateX, translateY);
        sgc.scale(scaleX, scaleY);

        sgc.drawImage(original, 0, 0);
        sgc.restore();

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return scratchCanvas.snapshot(params, null);
    }
}


