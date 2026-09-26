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
        if (imageFile != null) {
            try {
                Image image = new Image(imageFile.toURI().toString());

                canvas.widthProperty().unbind();
                canvas.heightProperty().unbind();

                imageView.setPreserveRatio(true);
                imageView.setImage(image);

                canvasContainer.setPrefSize(image.getWidth(), image.getHeight());
                canvas.setWidth(image.getWidth());
                canvas.setHeight(image.getHeight());

                canvas.widthProperty().bind(canvasContainer.widthProperty());
                canvas.heightProperty().bind(canvasContainer.heightProperty());

                canvasContainer.getChildren().removeIf(node -> node != canvas);
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                SaveFeature.setCurrentFile(imageFile);
                PaintApplication.isSaved = false;

            } catch (Exception error) {
                System.err.println("Error loading image file: " + error.getMessage());
                error.printStackTrace();
            }
        }
    }
}