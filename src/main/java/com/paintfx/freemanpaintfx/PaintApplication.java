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
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class PaintApplication extends Application {
    static boolean isSaved = false;

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
        MenuItem helpAct = new MenuItem("About Paint");

        help.getItems().addAll(helpAct);

        //Side Menu
        VBox sideMenu = new VBox(15);
        sideMenu.setStyle("-fx-padding: 15; -fx-background-color: #c5c7ca; -fx-pref-width: 160;");

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
        colorPicker.setValue(Color.web("#000000"));

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
        imageView.setPreserveRatio(true);

        //Canvas Create
        Canvas canvas = new Canvas(960, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //Image and Canvas Stack
        StackPane combineArea = new StackPane(imageView, canvas);

        //Large Image Handle
        ScrollPane imagePane = new ScrollPane(combineArea);
        imagePane.setPannable(true);
        imagePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        imagePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        //Give Brush Int & Color Hex
        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            gc.setLineWidth(newValue.doubleValue());
        });

        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            gc.setFill(newValue);
            gc.setStroke(newValue);
        });

        //MAIN LAYOUT
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setRight(sideMenu);
        root.setCenter(imagePane);

        //Open Image
        openImage.setOnAction(event -> {OpenFeature.open(imageView, primaryStage);});

        //Save Image
        saveImage.setOnAction(event -> {
            WritableImage combineImage = combineArea.snapshot(null,null);
            SaveFeature.save(combineImage, primaryStage);
            isSaved = true;
        });
        //Save As
        saveAsImage.setOnAction(event -> {
            WritableImage combineImage = combineArea.snapshot(null,null);
            SaveFeature.saveAs(combineImage, primaryStage);
        });

        //Draw Line
        pencilButton.setOnAction(event -> {
            CanvasFeature.drawLine(gc);
            isSaved = false;
        });

        //Close Intercept
        primaryStage.setOnCloseRequest(event -> {
            WritableImage combineImage = combineArea.snapshot(null,null);

            CloseInterceptFeature.handleExit(event, combineImage);
        });

        //Help Popup
        helpAct.setOnAction(event -> {
            Popup helpPopup = new Popup();

            HelpFeature.handleHelp(helpPopup, primaryStage);
        });

        //Showtime
        Scene scene = new Scene(root, 1150, 650);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}