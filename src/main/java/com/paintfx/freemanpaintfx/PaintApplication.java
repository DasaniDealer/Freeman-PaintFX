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
import javafx.scene.layout.Pane;
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
        RadioMenuItem lineButton = new RadioMenuItem("Line");
        RadioMenuItem dashButton = new RadioMenuItem("Dashed");
        ToggleButton eraserButton = new ToggleButton("Eraser");

        RadioMenuItem rectButton = new RadioMenuItem("Rectangle");
        RadioMenuItem fillRectButton = new RadioMenuItem("Filled Rectangle");

        //Toggle Groups
        ToggleGroup toolToggle = new ToggleGroup();
        pencilButton.setToggleGroup(toolToggle);

        MenuButton lineMenu = new MenuButton("Line");
        MenuButton shapeMenu = new MenuButton("Shape");
        MenuButton fillShapeMenu = new MenuButton("Filled Shape");

        ToggleGroup lineToggle = new ToggleGroup();
        lineButton.setToggleGroup(lineToggle);
        dashButton.setToggleGroup(lineToggle);

        ToggleGroup shapeToggle = new ToggleGroup();
        rectButton.setToggleGroup(shapeToggle);
        fillRectButton.setToggleGroup(shapeToggle);

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
                toolsLabel, pencilButton, lineMenu, shapeMenu, fillShapeMenu, eraserButton,
                colorLabel, colorPicker,
                sizeLabel, sizeSlider
        );

        lineMenu.getItems().addAll(lineButton, dashButton);
        shapeMenu.getItems().add(rectButton);
        fillShapeMenu.getItems().add(fillRectButton);

        //Image & Canvas Create
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);

        Pane canvasContainer = new Pane();

        Canvas canvas = new Canvas(1150, 650);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvasContainer.getChildren().addAll(canvas);

        //Connect Canvas to Container
        canvas.widthProperty().bind(canvasContainer.widthProperty());
        canvas.heightProperty().bind(canvasContainer.heightProperty());

        canvas.widthProperty().addListener((obs, oldInt, newInt) -> CanvasFeature.canvasResize(canvas, gc, oldInt.doubleValue(), canvas.getHeight()));
        canvas.heightProperty().addListener((obs, oldInt, newInt) -> CanvasFeature.canvasResize(canvas, gc, canvas.getWidth(), oldInt.doubleValue()));

        //Image and Canvas Stack
        StackPane combineArea = new StackPane(imageView, canvasContainer);

        ScrollPane imagePane = new ScrollPane(combineArea);
        imagePane.setPannable(false);
        imagePane.setFitToWidth(true);
        imagePane.setFitToHeight(true);
        imagePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        imagePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);;

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
        openImage.setOnAction(event -> {
            OpenFeature.open(imageView, primaryStage, gc, canvas);
        });

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

        toolToggle.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            canvas.setOnMousePressed(null);
            canvas.setOnMouseDragged(null);
            canvas.setOnMouseReleased(null);

            if (newToggle == null) {return;}

            //Unselect Dropdown Items
            if (newToggle == pencilButton) {
                lineToggle.selectToggle(null);
                shapeToggle.selectToggle(null);
            }

            //Toggle Button Effects
            if (newToggle == pencilButton) {CanvasFeature.drawLine(gc, canvas);}

            isSaved = false;
        });

        lineToggle.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            canvas.setOnMousePressed(null);
            canvas.setOnMouseDragged(null);
            canvas.setOnMouseReleased(null);

            if (newToggle == null) return;

            //Unselect Main Menu Items
            toolToggle.selectToggle(null);
            shapeToggle.selectToggle(null);

            if (newToggle == lineButton) {CanvasFeature.drawStraight(gc, canvas);}
            else if (newToggle == dashButton) {CanvasFeature.drawDashed(gc, canvas);}
            isSaved = false;
        });

        shapeToggle.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            boolean filled = false;

            canvasContainer.setOnMousePressed(null);
            canvasContainer.setOnMouseDragged(null);
            canvasContainer.setOnMouseReleased(null);

            if (newToggle == null) return;

            //Unselect Main Menu Items
            toolToggle.selectToggle(null);
            lineToggle.selectToggle(null);

            if (newToggle == rectButton) {
                filled = false;
                ShapeFeature.rectDraw(canvasContainer, colorPicker, sizeSlider, filled);
            }

            if (newToggle == fillRectButton) {
                filled = true;
                ShapeFeature.rectDraw(canvasContainer, colorPicker, sizeSlider, filled);
            }

            isSaved = false;
        });

        //Help Popup
        helpAct.setOnAction(event -> {
            Popup helpPopup = new Popup();

            HelpFeature.handleHelp(helpPopup, primaryStage);
        });

        //Close Intercept
        primaryStage.setOnCloseRequest(event -> {
            WritableImage combineImage = combineArea.snapshot(null,null);

            CloseInterceptFeature.handleExit(event, combineImage);
        });

        //SHOWTIME
        Scene scene = new Scene(root, 1150, 650);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}