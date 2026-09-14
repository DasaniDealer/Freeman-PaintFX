package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.GraphicsContext;

public class CanvasFeature {

    static void drawLines(GraphicsContext gc) {

        gc.beginPath();
        gc.moveTo(30.5, 30.5);
        gc.lineTo(150.5, 30.5);
        gc.stroke();
    }
}
