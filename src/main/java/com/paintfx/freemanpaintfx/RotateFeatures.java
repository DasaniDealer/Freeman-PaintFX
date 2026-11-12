package com.paintfx.freemanpaintfx;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static com.paintfx.freemanpaintfx.SelectSettings.*;

public class RotateFeatures {
    private static WritableImage transparentSnapshot(Canvas canvas) {
        int w = (int) Math.round(canvas.getWidth());
        int h = (int) Math.round(canvas.getHeight());

        WritableImage target = new WritableImage(w, h);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        params.setViewport(new Rectangle2D(0, 0, w, h));

        return canvas.snapshot(params, target);
    }

    private static boolean isDimensionSwapNeeded(double degrees) {
        int norm = (((int) Math.round(degrees) % 360) + 360) % 360;
        return norm == 90 || norm == 270;
    }

    // Handle Rotate
    public static void handleRotate(TabFeature.TabRecord record, double degrees) {
        Canvas mainCanvas = record.mainCanvas();
        Canvas prevCanvas = record.prevCanvas();
        GraphicsContext gc = record.gc();
        GraphicsContext pgc = record.pgc();

        if (selectionSnapshot != null) {
            selectionSnapshot = snapRotate(selectionSnapshot, degrees);

            // Only swap width/height for 90 and 270 degrees (not 180)
            if (isDimensionSwapNeeded(degrees)) {
                double currentCenterX = selectX + (selectW / 2.0);
                double currentCenterY = selectY + (selectH / 2.0);

                double tempW = selectW;
                selectW = selectH;
                selectH = tempW;

                selectX = Math.round(currentCenterX - (selectW / 2.0));
                selectY = Math.round(currentCenterY - (selectH / 2.0));
            }

            pgc.setImageSmoothing(false);
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
            pgc.drawImage(selectionSnapshot, Math.round(selectX), Math.round(selectY));

            pgc.setStroke(Color.BLUE);
            pgc.setLineWidth(1);
            pgc.setLineDashes(5.0);
            pgc.strokeRect(Math.round(selectX), Math.round(selectY), selectW, selectH);
        } else {
            CanvasHistory.saveState(mainCanvas);

            fullRotate(mainCanvas, gc, degrees);
            fullRotate(prevCanvas, pgc, degrees);
        }
    }

    // Rotate 90/180/270 Degrees (Lossless Pixel-by-Pixel)
    static void fullRotate(Canvas canvas, GraphicsContext gc, double degrees) {
        WritableImage snapshot = transparentSnapshot(canvas);
        WritableImage rotated = snapRotate(snapshot, degrees);

        gc.setImageSmoothing(false);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.setWidth(rotated.getWidth());
        canvas.setHeight(rotated.getHeight());
        gc.drawImage(rotated, 0, 0);
    }

    // Rotate Section (Lossless Pixel-by-Pixel)
    private static WritableImage snapRotate(Image original, double degrees) {
        if (original == null) return null;

        int w = (int) Math.round(original.getWidth());
        int h = (int) Math.round(original.getHeight());
        int norm = (((int) Math.round(degrees) % 360) + 360) % 360;

        PixelReader reader = original.getPixelReader();
        WritableImage dest = (norm == 90 || norm == 270)
                ? new WritableImage(h, w)
                : new WritableImage(w, h);
        PixelWriter writer = dest.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = reader.getArgb(x, y);
                if (norm == 90) {
                    writer.setArgb(h - 1 - y, x, argb);
                } else if (norm == 180) {
                    writer.setArgb(w - 1 - x, h - 1 - y, argb);
                } else if (norm == 270) {
                    writer.setArgb(y, w - 1 - x, argb);
                } else {
                    writer.setArgb(x, y, argb);
                }
            }
        }
        return dest;
    }

    // Handle Flip
    public static void handleFlip(TabFeature.TabRecord record, boolean hori, boolean vert) {
        Canvas mainCanvas = record.mainCanvas();
        Canvas prevCanvas = record.prevCanvas();
        GraphicsContext gc = record.gc();
        GraphicsContext pgc = record.pgc();
        if (selectionSnapshot != null) {
            selectionSnapshot = snapFlip(selectionSnapshot, hori, vert);

            pgc.setImageSmoothing(false);
            pgc.clearRect(0, 0, prevCanvas.getWidth(), prevCanvas.getHeight());
            pgc.drawImage(selectionSnapshot, Math.round(selectX), Math.round(selectY));

            pgc.setStroke(Color.BLUE);
            pgc.setLineWidth(1);
            pgc.setLineDashes(5.0);
            pgc.strokeRect(Math.round(selectX), Math.round(selectY), selectW, selectH);
        } else {
            CanvasHistory.saveState(mainCanvas);

            fullFlip(mainCanvas, gc, hori, vert);
            fullFlip(prevCanvas, pgc, hori, vert);
        }
    }

    // Flip Full Canvas (Lossless Pixel-by-Pixel)
    private static void fullFlip(Canvas canvas, GraphicsContext gc, boolean hori, boolean vert) {
        WritableImage snapshot = transparentSnapshot(canvas);
        WritableImage flipped = snapFlip(snapshot, hori, vert);

        gc.setImageSmoothing(false);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(flipped, 0, 0);
    }

    // Flip Section (Lossless Pixel-by-Pixel)
    private static WritableImage snapFlip(Image original, boolean hori, boolean vert) {
        if (original == null) return null;

        int w = (int) Math.round(original.getWidth());
        int h = (int) Math.round(original.getHeight());

        PixelReader reader = original.getPixelReader();
        WritableImage dest = new WritableImage(w, h);
        PixelWriter writer = dest.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int destX = hori ? (w - 1 - x) : x;
                int destY = vert ? (h - 1 - y) : y;
                writer.setArgb(destX, destY, reader.getArgb(x, y));
            }
        }
        return dest;
    }
}