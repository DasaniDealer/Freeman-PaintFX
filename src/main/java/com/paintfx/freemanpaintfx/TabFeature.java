package com.paintfx.freemanpaintfx;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class TabFeature {
    private static int tabCount = 0;

    public static void addTab(TabPane tabPane, DrawSettings drawSettings) {
        tabCount++;

        Tab newTab = new Tab("Tab " + tabCount);

        //Image & Canvas Create
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);

        Pane canvasContainer = new Pane();

        Canvas mainCanvas = new Canvas(1150, 650);
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        Canvas prevCanvas = new Canvas(1150, 650);
        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

        canvasContainer.getChildren().addAll(mainCanvas,prevCanvas);

        //Connect Canvas to Container
        mainCanvas.widthProperty().bind(canvasContainer.widthProperty());
        mainCanvas.heightProperty().bind(canvasContainer.heightProperty());

        prevCanvas.widthProperty().bind(canvasContainer.widthProperty());
        prevCanvas.heightProperty().bind(canvasContainer.heightProperty());

        canvasContainer.widthProperty().addListener((obs, oldW, newW) -> {
            double targetWidth = newW.doubleValue();
            double currentHeight = canvasContainer.getHeight();

            MiscHandles.canvasResize(mainCanvas, targetWidth, currentHeight);
            MiscHandles.canvasResize(prevCanvas, targetWidth, currentHeight);
        });

        canvasContainer.heightProperty().addListener((obs, oldH, newH) -> {
            double currentWidth = canvasContainer.getWidth();
            double targetHeight = newH.doubleValue();

            MiscHandles.canvasResize(mainCanvas, currentWidth, targetHeight);
            MiscHandles.canvasResize(prevCanvas, currentWidth, targetHeight);
        });

        //Image and Canvas Stack
        StackPane combineArea = new StackPane(imageView, canvasContainer);

        ScrollPane imagePane = new ScrollPane(combineArea);

        imagePane.setPannable(false);
        imagePane.setFitToWidth(true);
        imagePane.setFitToHeight(true);
        imagePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        imagePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        newTab.setUserData(new TabRecord(
                imageView,
                mainCanvas,
                prevCanvas,
                canvasContainer,
                gc,
                pgc,
                combineArea
        ));

        newTab.setContent(imagePane);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }

    public record TabRecord(
            ImageView imageView,
            Canvas mainCanvas,
            Canvas prevCanvas,
            Pane canvasContainer,
            GraphicsContext gc,
            GraphicsContext pgc,
            StackPane combineArea
    ){}
}
