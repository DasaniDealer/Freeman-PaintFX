package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class TabFeature {
    private static int tabCount = 0;

    public static void addTab(TabPane tabPane, DrawSettings drawSettings) {
        tabCount++;

        Tab newTab = new Tab("Tab " + tabCount);

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

        canvas.widthProperty().addListener((obs, oldInt, newInt) ->
                CanvasFeature.canvasResize(canvas, gc, oldInt.doubleValue(), canvas.getHeight()));
        canvas.heightProperty().addListener((obs, oldInt, newInt) ->
                CanvasFeature.canvasResize(canvas, gc, canvas.getWidth(), oldInt.doubleValue()));

        //Image and Canvas Stack
        StackPane combineArea = new StackPane(imageView, canvasContainer);

        ScrollPane imagePane = new ScrollPane(combineArea);
        imagePane.setPannable(false);
        imagePane.setFitToWidth(true);
        imagePane.setFitToHeight(true);
        imagePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        imagePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        newTab.setUserData(new TabRecord(imageView, canvas, canvasContainer, gc, combineArea));

        newTab.setContent(imagePane);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
    }

    public record TabRecord(ImageView imageView, Canvas canvas, Pane canvasContainer, GraphicsContext gc, StackPane combineArea) {}
}
