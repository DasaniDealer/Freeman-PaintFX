package com.paintfx.freemanpaintfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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

        //Help Submenu
        MenuItem helpAct = new MenuItem("Help");

        help.getItems().addAll(helpAct);

        //Side Menu
        VBox sideMenu = new VBox(15); // 15px spacing between items
        sideMenu.setStyle("-fx-padding: 15; -fx-background-color: #E0E0E0; -fx-pref-width: 160;");

        Label toolsLabel = new Label("Tools");
        toolsLabel.setStyle("-fx-font-weight: bold;");
        ToggleButton pencilButton = new ToggleButton("Pencil");
        ToggleButton eraserButton = new ToggleButton("Eraser");

        ToggleGroup toolGroup = new ToggleGroup();
        pencilButton.setToggleGroup(toolGroup);
        eraserButton.setToggleGroup(toolGroup);
        pencilButton.setSelected(true);

        //Colours
        Label colorLabel = new Label("Colour");
        colorLabel.setStyle("-fx-font-weight: bold;");
        ColorPicker colorPicker = new ColorPicker();

        //Brush Size
        Label sizeLabel = new Label("Brush Size");
        sizeLabel.setStyle("-fx-font-weight: bold;");
        Slider sizeSlider = new Slider(1, 50, 5);
        sizeSlider.setShowTickLabels(true);

        //Side Menu Placements
        sideMenu.getChildren().addAll(
                toolsLabel, pencilButton, eraserButton,
                colorLabel, colorPicker,
                sizeLabel, sizeSlider
        );

        //Image Create
        ImageView imageView = new ImageView();

        imageView.setFitWidth(960);
        imageView.setFitHeight(600);
        imageView.setPreserveRatio(true);

        //Canvas Create
        Canvas canvas = new Canvas(960, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //Image and Canvas Stack
        StackPane combineArea = new StackPane(imageView, canvas);

        //Observe & Give Brush Int
        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double widthSize = newValue.doubleValue();
            gc.setLineWidth(widthSize);
        });

        //MAIN LAYOUT
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setRight(sideMenu);
        root.setCenter(combineArea);

        //Open Image
        openImage.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select an Image File");

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );

            File imageFile = fileChooser.showOpenDialog(primaryStage);
            if (imageFile != null) {
                Image image = new Image(imageFile.toURI().toString());

                imageView.setImage(image);

                SaveFeature.setCurrentFile(imageFile);
            }

        });

        //Save Image
        saveImage.setOnAction(event -> {
            WritableImage combineImage = combineArea.snapshot(null,null);
            SaveFeature.save(combineImage, primaryStage);
        });
        //Save As
        saveAsImage.setOnAction(event -> {
                WritableImage combineImage = combineArea.snapshot(null,null);
                SaveFeature.saveAs(combineImage, primaryStage);
        });

        //Canvas
        pencilButton.setOnAction(event -> CanvasFeature.drawLines(gc));

        //Showtime
        Scene scene = new Scene(root, 1150, 650);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}