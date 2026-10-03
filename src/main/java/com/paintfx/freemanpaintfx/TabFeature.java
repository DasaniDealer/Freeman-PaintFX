package com.paintfx.freemanpaintfx;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class TabFeature {
    private static int tabCount = 0;

    public static void addTab(TabPane tabPane) {
        tabCount++;

        Tab newTab = new Tab("Tab " + tabCount);

        //Image & Canvas Create
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setMouseTransparent(true);

        Group canvasContainer = new Group();

        Canvas mainCanvas = new Canvas(1150, 650);
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        Canvas prevCanvas = new Canvas(1150, 650);
        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

        canvasContainer.getChildren().addAll(mainCanvas,prevCanvas);

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
            Group canvasContainer,
            GraphicsContext gc,
            GraphicsContext pgc,
            StackPane combineArea
    ){}
}
