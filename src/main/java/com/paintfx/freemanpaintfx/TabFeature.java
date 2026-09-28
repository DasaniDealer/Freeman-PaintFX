package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.StrokeLineCap;

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
            CanvasFeature.canvasResize(mainCanvas, gc, oldW.doubleValue(), canvasContainer.getHeight());
            CanvasFeature.canvasResize(prevCanvas, pgc, oldW.doubleValue(), canvasContainer.getHeight());
        });

        canvasContainer.heightProperty().addListener((obs, oldH, newH) -> {
            CanvasFeature.canvasResize(mainCanvas, gc, canvasContainer.getWidth(), oldH.doubleValue());
            CanvasFeature.canvasResize(prevCanvas, pgc, canvasContainer.getWidth(), oldH.doubleValue());
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

    static void applySettings(GraphicsContext gc, DrawSettings drawSettings, boolean dashed) {
        gc.setStroke(drawSettings.getColor());
        gc.setLineWidth(drawSettings.getSize());
        gc.setLineCap(StrokeLineCap.ROUND);

        if (dashed) {
            double lineWidth = drawSettings.getSize();
            gc.setLineDashes(3 * lineWidth, 2 * lineWidth);
        } else {
            gc.setLineDashes((double[]) null);
        }
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
