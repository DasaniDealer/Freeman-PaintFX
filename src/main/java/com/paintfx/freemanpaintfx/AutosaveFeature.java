package com.paintfx.freemanpaintfx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class AutosaveFeature {
    public static Timeline autosaveTimeline;

    public static void startAutosaveTimer(Canvas canvas, int seconds) {
        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(seconds),
                event -> canvasSnapshot(canvas)
        );

        autosaveTimeline = new Timeline(keyFrame);
        autosaveTimeline.setCycleCount(Animation.INDEFINITE);
        autosaveTimeline.play();
    }

    private static void canvasSnapshot(Canvas canvas){
        WritableImage writableImage = canvas.snapshot(null, null);

        Thread saveThread = new Thread(() -> {
            File file = new File("canvas-autosave.png");
            try {
                //Convert Image
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                System.out.println("Autosaved successfully to: " + file.getAbsolutePath());
            }
            catch (IOException e) {
                System.err.println("Failed to autosave canvas: " + e.getMessage());
            }
        });

        saveThread.setDaemon(true);
        saveThread.start();
    }
}
