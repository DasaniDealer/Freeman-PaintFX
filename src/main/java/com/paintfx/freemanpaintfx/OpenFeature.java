package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class OpenFeature {
    public static void open(ImageView imageView, Stage primaryStage, GraphicsContext gc, Canvas canvas, Pane canvasContainer) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        File imageFile = fileChooser.showOpenDialog(primaryStage);
        try {
            if (imageFile != null) {
                Image image = new Image(imageFile.toURI().toString());

                imageView.setImage(image);
                imageView.setFitHeight(image.getHeight());
                imageView.setFitWidth(image.getWidth());

                canvasContainer.getChildren().clear();
                canvasContainer.getChildren().add(canvas);

                imageView.setPreserveRatio(true);

                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                SaveFeature.setCurrentFile(imageFile);
                PaintApplication.isSaved = false;
            }
        }

        catch (Exception error) {
            System.err.println("Error loading image file: " + error.getMessage());
        }
    }
}
