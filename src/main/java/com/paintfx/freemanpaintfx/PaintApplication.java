package com.paintfx.freemanpaintfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import java.io.File;
import java.io.IOException;


public class PaintApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        primaryStage.setTitle("(Pain)t");

        //Menu Bar
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu("File");
        Menu options = new Menu("Options");
        Menu help = new Menu("Help");

        menuBar.getMenus().addAll(file, options, help);

        //File Submenu
        MenuItem openImage = new MenuItem("Open");
        MenuItem saveImage = new MenuItem("Save");
        MenuItem saveAsImage = new MenuItem("Save As");

        file.getItems().addAll(openImage, saveImage, saveAsImage);

        //Image Create
        BorderPane root = new BorderPane();
        ImageView imageView = new ImageView();
        root.setCenter(imageView);


        //Open Image
        openImage.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select an Image File");

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );

            File imageFile = fileChooser.showOpenDialog(primaryStage);
            if (imageFile != null) {
                Image image = new Image(imageFile.toURI().toString());

                imageView.setFitWidth(960);
                imageView.setFitHeight(600);
                imageView.setPreserveRatio(true);

                imageView.setImage(image);

                SaveFeature.setCurrentFile(imageFile);
            }

        });

        //Save Image
        saveImage.setOnAction(event -> SaveFeature.save(imageView.getImage(), primaryStage));
        //Save As
        saveAsImage.setOnAction(event -> SaveFeature.saveAs(imageView.getImage(), primaryStage));

        //IMPORTANT VBOX
        VBox vBox = new VBox(menuBar, root);
        Scene scene = new Scene(vBox, 960, 600);

        //Showtime
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}