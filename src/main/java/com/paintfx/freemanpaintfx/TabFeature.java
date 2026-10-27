package com.paintfx.freemanpaintfx;

import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class TabFeature {
    private static int tabCount = 0;

    public static final Map<String, Canvas> sharedCanvases = new HashMap<>();
    private static WebserverFeature serverInstance;

    private final static int boundWidth = 1150;
    private final static int boundHeight = 650;

    public static void addTab(TabPane tabPane) {
        tabCount++;
        String tabTitle = "Tab " + tabCount;
        Tab newTab = new Tab(tabTitle);

        //Image & Canvas Create
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setMouseTransparent(true);

        Group canvasContainer = new Group();

        //Find Monitor Bounds
        Canvas mainCanvas = new Canvas(boundWidth, boundHeight);
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        Canvas prevCanvas = new Canvas(boundWidth, boundHeight);
        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

        canvasContainer.getChildren().addAll(mainCanvas,prevCanvas);

        //Image and Canvas Stack
        StackPane combineArea = new StackPane(imageView, canvasContainer);
        ScrollPane imagePane = new ScrollPane(combineArea);

        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());

        //Autosaving
        Timeline tabTimeline = AutosaveFeature.startAutosaveTimer(mainCanvas, 60);

        imagePane.setPannable(false);
        imagePane.setFitToWidth(true);
        imagePane.setFitToHeight(true);
        imagePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        imagePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        //Remove From Webserver
        newTab.setOnClosed(event -> {
            if (tabTimeline != null) {
                tabTimeline.stop();
                System.out.println("Autosave stopped for: " + tabTitle);
            }

            sharedCanvases.remove(tabTitle);
            if (serverInstance != null) {
                serverInstance.updateSelection(sharedCanvases);
            }
        });

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

        sharedCanvases.put(tabTitle, mainCanvas);
        if (serverInstance != null) {
            serverInstance.updateSelection(sharedCanvases);
        }
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
