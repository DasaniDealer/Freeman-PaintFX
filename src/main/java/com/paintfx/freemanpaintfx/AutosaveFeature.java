package com.paintfx.freemanpaintfx;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class AutosaveFeature {
    public static Timeline autosaveTimeline;

    public static Timeline startAutosaveTimer(Canvas canvas, int seconds) {
        stopAutosave();

        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(seconds),
                event -> canvasSnapshot(canvas)
        );

        autosaveTimeline = new Timeline(keyFrame);
        autosaveTimeline.setCycleCount(Animation.INDEFINITE);
        autosaveTimeline.play();

        if (canvas.getScene() != null && canvas.getScene().getWindow() != null) {
            Stage ownerStage = (Stage) canvas.getScene().getWindow();
            showNoti(ownerStage, "Autosave Enabled", "Your progress will be saved every " + seconds + "s.", "#2ecc71");
        } else {
            canvas.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.windowProperty().addListener((obsWin, oldWin, newWin) -> {
                        if (newWin instanceof Stage) {
                            showNoti((Stage) newWin, "Autosave Enabled", "Your progress will be saved every " + seconds + "s.", "#2ecc71");
                        }
                    });
                }
            });
        }

        return autosaveTimeline;
    }

    private static void canvasSnapshot(Canvas canvas){
        WritableImage writableImage = canvas.snapshot(null, null);
        Stage ownerStage = (Stage) canvas.getScene().getWindow();

        Thread saveThread = new Thread(() -> {
            File file = new File("canvas-autosave.png");
            try {
                //Convert Image
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                System.out.println("Autosaved successfully to: " + file.getAbsolutePath());
                Platform.runLater(() ->
                        showNoti(ownerStage, "Autosave Complete", "Progress secured successfully.", "#34495e")
                );
            }
            catch (IOException e) {
                System.err.println("Failed to autosave canvas: " + e.getMessage());
                Platform.runLater(() ->
                        showNoti(ownerStage, "Autosave Failed", e.getMessage(), "#e74c3c")
                );
            }
        });

        saveThread.setDaemon(true);
        saveThread.start();
    }

    public static void stopAutosave() {
        if (autosaveTimeline != null) {
            autosaveTimeline.stop();
            autosaveTimeline = null;
        }
    }

    private static void showNoti(Stage owner, String title, String message, String accentColorHex) {
        if (owner == null) return;

        Stage toastStage = new Stage();
        toastStage.initOwner(owner);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        //Title Label
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        //Message Label
        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 11px;");
        msgLabel.setWrapText(true);

        //Layout Container
        VBox toastRoot = new VBox(3, titleLabel, msgLabel);
        toastRoot.setAlignment(Pos.TOP_LEFT);
        toastRoot.setPadding(new Insets(10, 20, 10, 20));
        toastRoot.setPrefWidth(220);
        toastRoot.setStyle(String.format(
                "-fx-background-color: #2c3e50; " +
                        "-fx-background-radius: 4; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 0 0 0 4; " +
                        "-fx-border-radius: 4;",
                accentColorHex
        ));

        Scene scene = new Scene(toastRoot);
        scene.setFill(null);
        toastStage.setScene(scene);

        toastRoot.applyCss();
        toastRoot.layout();

        double margin = 25.0;
        double targetX = owner.getX() + owner.getWidth() - toastStage.getWidth() - margin;
        double targetY = owner.getY() + owner.getHeight() - toastStage.getHeight() - margin;

        toastStage.setX(targetX);
        toastStage.setY(targetY);

        //Transition
        FadeTransition fade = new FadeTransition(Duration.millis(400), toastRoot);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.seconds(2.0));
        fade.setOnFinished(e -> toastStage.close());

        toastStage.show();
        fade.play();
    }
}
