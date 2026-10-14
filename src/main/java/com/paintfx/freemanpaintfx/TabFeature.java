package com.paintfx.freemanpaintfx;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class TabFeature {
    private static int tabCount = 0;

    public static final Map<String, Canvas> sharedCanvases = new HashMap<>();
    private static WebserverFeature serverInstance;

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
        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        double monitorWidth = screenBounds.getWidth();
        double monitorHeight = screenBounds.getHeight();

        Canvas mainCanvas = new Canvas(monitorWidth, monitorHeight);
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();

        Canvas prevCanvas = new Canvas(monitorWidth, monitorHeight);
        GraphicsContext pgc = prevCanvas.getGraphicsContext2D();

        canvasContainer.getChildren().addAll(mainCanvas,prevCanvas);

        //Image and Canvas Stack
        StackPane combineArea = new StackPane(imageView, canvasContainer);

        ScrollPane imagePane = new ScrollPane(combineArea);

        //Autosaving
        AutosaveFeature.startAutosaveTimer(mainCanvas, 60);

        imagePane.setPannable(false);
        imagePane.setFitToWidth(true);
        imagePane.setFitToHeight(true);
        imagePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        imagePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        //Remove From Webserver
        newTab.setOnClosed(event -> {
            //Remove from shared inventory
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
