package com.paintfx.freemanpaintfx;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.Stack;

public class CanvasHistory {
    // Stacks to track Undo and Redo states as snapshots
    private static final Stack<WritableImage> undoStack = new Stack<>();
    private static final Stack<WritableImage> redoStack = new Stack<>();

    /**
     * Captures the current state of the main canvas and saves it to the Undo stack.
     * Call this BEFORE any tool modifies the main canvas.
     */
    public static void saveState(Canvas mainCanvas) {
        if (mainCanvas == null) return;

        int width = (int) mainCanvas.getWidth();
        int height = (int) mainCanvas.getHeight();
        if (width <= 0 || height <= 0) return;

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        WritableImage snapshot = new WritableImage(width, height);
        mainCanvas.snapshot(params, snapshot);

        undoStack.push(snapshot);
        redoStack.clear();
    }

    public static void undo(Canvas mainCanvas) {
        if (undoStack.isEmpty() || mainCanvas == null) return;

        // Push current state to redo stack before applying the undo change
        int width = (int) mainCanvas.getWidth();
        int height = (int) mainCanvas.getHeight();
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage currentSnapshot = new WritableImage(width, height);
        mainCanvas.snapshot(params, currentSnapshot);
        redoStack.push(currentSnapshot);

        // Pop previous state and draw it
        WritableImage previousState = undoStack.pop();
        restoreCanvas(mainCanvas, previousState);
    }

    public static void redo(Canvas mainCanvas) {
        if (redoStack.isEmpty() || mainCanvas == null) return;

        // Push current state back to undo stack before restoring
        int width = (int) mainCanvas.getWidth();
        int height = (int) mainCanvas.getHeight();
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage currentSnapshot = new WritableImage(width, height);
        mainCanvas.snapshot(params, currentSnapshot);
        undoStack.push(currentSnapshot);

        // Pop next state and draw it
        WritableImage nextState = redoStack.pop();
        restoreCanvas(mainCanvas, nextState);
    }

    private static void restoreCanvas(Canvas canvas, WritableImage state) {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.getGraphicsContext2D().drawImage(state, 0, 0);
    }

    public static void clearHistory() {
        undoStack.clear();
        redoStack.clear();
    }
}
