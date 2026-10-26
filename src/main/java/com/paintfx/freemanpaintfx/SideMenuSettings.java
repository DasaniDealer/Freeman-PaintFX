package com.paintfx.freemanpaintfx;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class SideMenuSettings {
    private final VBox sideMenu;
    private boolean isSaved;

    private void triggerAction(TabPane tabPane, java.util.function.Consumer<TabFeature.TabRecord> action) {
        Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
        if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
            action.accept(context);
            isSaved = false;
        }
    }

    public SideMenuSettings(TabPane tabPane, DrawSettings drawSettings) {
        sideMenu = new VBox(10);
        sideMenu.setStyle("-fx-padding: 10; -fx-background-color: #c5c7ca; -fx-pref-width: 160;");

        Label toolsLabel = new Label("Draw Tools");
        toolsLabel.setStyle("-fx-font-weight: bold;");

        //Undo Redo Buttons
        Button undoButton = new Button("", new FontIcon(FontAwesomeSolid.UNDO));
        Button redoButton = new Button("", new FontIcon(FontAwesomeSolid.REDO));

        undoButton.setOnAction(event -> triggerAction(tabPane, context -> CanvasHistory.undo(context.mainCanvas())));
        redoButton.setOnAction(event -> triggerAction(tabPane, context -> CanvasHistory.redo(context.mainCanvas())));

        ToggleButton selectButton = new ToggleButton("", new FontIcon(FontAwesomeSolid.VECTOR_SQUARE));
        ToggleButton pencilButton = new ToggleButton("", new FontIcon(FontAwesomeSolid.PEN));
        ToggleButton eraserButton = new ToggleButton("", new FontIcon(FontAwesomeSolid.ERASER));
        ToggleButton textButton = new ToggleButton("", new FontIcon(FontAwesomeSolid.FONT));
        ToggleButton grabButton = drawSettings.getGrabButton();
        Button clearButton = new Button("", new FontIcon(FontAwesomeSolid.TRASH_ALT));

        RadioMenuItem lineButton = new RadioMenuItem("Filled");
        RadioMenuItem dashButton = new RadioMenuItem("Dashed");

        RadioMenuItem squareButton = new RadioMenuItem("Square");
        RadioMenuItem dashSquareButton = new RadioMenuItem("Square");
        RadioMenuItem rectButton = new RadioMenuItem("Rectangle");
        RadioMenuItem dashRectButton = new RadioMenuItem("Rectangle");

        RadioMenuItem triangleButton = new RadioMenuItem("Triangle");
        RadioMenuItem dashTriangleButton = new RadioMenuItem("Triangle");
        RadioMenuItem rightTriangleButton = new RadioMenuItem("Right Triangle");
        RadioMenuItem dashRightTriangleButton = new RadioMenuItem("Right Triangle");

        RadioMenuItem pentagonButton = new RadioMenuItem("Pentagon");
        RadioMenuItem dashPentagonButton = new RadioMenuItem("Pentagon");

        RadioMenuItem circleButton = new RadioMenuItem("Circle");
        RadioMenuItem dashCircleButton = new RadioMenuItem("Circle");
        RadioMenuItem ellipseButton = new RadioMenuItem("Ellipse");
        RadioMenuItem dashEllipseButton = new RadioMenuItem("Ellipse");

        RadioMenuItem fillSquareButton = new RadioMenuItem("Square");
        RadioMenuItem fillRectButton = new RadioMenuItem("Rectangle");
        RadioMenuItem fillTriangleButton = new RadioMenuItem("Triangle");
        RadioMenuItem fillRightTriangleButton = new RadioMenuItem("Right Triangle");
        RadioMenuItem fillPentagonButton = new RadioMenuItem("Pentagon");
        RadioMenuItem fillCircleButton = new RadioMenuItem("Circle");
        RadioMenuItem fillEllipseButton = new RadioMenuItem("Ellipse");
        //Custom Polygon Button
        RadioMenuItem polygonButton = new RadioMenuItem("Polygon");
        RadioMenuItem dashPolygonButton = new RadioMenuItem("Dashed Polygon");
        RadioMenuItem fillPolygonButton = new RadioMenuItem("Filled Polygon");
        Spinner<Integer> polygonSpinner = new Spinner<>(3, 20, 5);
        polygonSpinner.setEditable(true);

        RadioMenuItem starButton = new RadioMenuItem("Star");
        RadioMenuItem dashStarButton = new RadioMenuItem("Dashed Star");
        RadioMenuItem fillStarButton = new RadioMenuItem("Filled Star");
        Spinner<Integer> starSpinner = new Spinner<>(3, 20, 5);
        starSpinner.setEditable(true);

        //Rotate & Flip Buttons
        Button rotate90Button = new Button("90°");
        Button rotate180Button = new Button("180°");
        Button rotate270Button = new Button("270°");

        Button horiFlipButton = new Button("", new FontIcon(FontAwesomeSolid.EXCHANGE_ALT));
        Button vertFlipButton = new Button("", new FontIcon(FontAwesomeSolid.SORT));

        //Menu Groups
        MenuButton lineMenu = new MenuButton("╱");
        MenuButton shapeMenu = new MenuButton("□");
        MenuButton dashMenu = new MenuButton("⬚");
        MenuButton fillShapeMenu = new MenuButton("■");
        MenuButton polygonMenu = new MenuButton("", new FontIcon(FontAwesomeSolid.SHAPES));
        MenuButton starMenu = new MenuButton("", new FontIcon(FontAwesomeSolid.STAR));

        //Toggle Groups
        ToggleGroup toolToggle = new ToggleGroup();
        ToggleGroup shapeToggle = new ToggleGroup();

        toolToggle.getToggles().addAll(selectButton, pencilButton, eraserButton,
                textButton, grabButton,
                polygonButton, dashPolygonButton, fillPolygonButton,
                starButton, dashStarButton, fillStarButton,
                lineButton, dashButton);

        //Shape Menu
        shapeToggle.getToggles().addAll(squareButton, dashSquareButton, fillSquareButton,
                rectButton, dashRectButton, fillRectButton,
                triangleButton, dashTriangleButton, fillTriangleButton,
                rightTriangleButton, dashRightTriangleButton, fillRightTriangleButton,
                pentagonButton, dashPentagonButton, fillPentagonButton,
                circleButton, dashCircleButton, fillCircleButton,
                ellipseButton, dashEllipseButton, fillEllipseButton);

        //HBox Settings
        HBox undoRedoBox = new HBox(10);
        undoRedoBox.setAlignment(Pos.CENTER_LEFT);
        undoRedoBox.getChildren().addAll(undoButton, redoButton);

        HBox drawBox = new HBox(10);
        drawBox.setAlignment(Pos.CENTER_LEFT);
        drawBox.getChildren().addAll(pencilButton, lineMenu, eraserButton);

        HBox extraBox = new HBox(10);
        extraBox.setAlignment(Pos.CENTER_LEFT);
        extraBox.getChildren().addAll(textButton, selectButton, clearButton);

        HBox shapeBox = new HBox(5);
        shapeBox.setAlignment(Pos.CENTER_LEFT);
        shapeBox.getChildren().addAll(shapeMenu, dashMenu, fillShapeMenu);

        HBox polygonBox = new HBox(10);
        polygonBox.setAlignment(Pos.CENTER_LEFT);
        polygonBox.getChildren().addAll(polygonSpinner, polygonMenu);

        HBox starBox = new HBox(10);
        polygonBox.setAlignment(Pos.CENTER_LEFT);
        starBox.getChildren().addAll(starSpinner, starMenu);

        HBox rotateBox = new HBox(10);
        rotateBox.setAlignment(Pos.CENTER_LEFT);
        rotateBox.getChildren().addAll(rotate90Button, rotate180Button, rotate270Button);

        HBox flipBox = new HBox(10);
        flipBox.setAlignment(Pos.CENTER_LEFT);
        flipBox.getChildren().addAll(horiFlipButton, vertFlipButton);

        //Clear New Tab Listeners
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                //Clear Toggles
                toolToggle.selectToggle(null);
                shapeToggle.selectToggle(null);

                if (oldTab != null && oldTab.getUserData() instanceof TabFeature.TabRecord oldContext) {
                    oldContext.canvasContainer().setOnMousePressed(null);
                    oldContext.canvasContainer().setOnMouseDragged(null);
                    oldContext.canvasContainer().setOnMouseReleased(null);
                }
            }
        });

        //Side Menu Placements
        sideMenu.getChildren().addAll(
                toolsLabel, undoRedoBox, drawBox, extraBox,
                shapeBox, polygonBox, starBox,
                rotateBox, flipBox, drawSettings
        );

        lineMenu.getItems().addAll(lineButton, dashButton);
        shapeMenu.getItems().addAll(squareButton, rectButton, triangleButton, rightTriangleButton, pentagonButton, circleButton, ellipseButton);
        dashMenu.getItems().addAll(dashSquareButton, dashRectButton, dashTriangleButton, dashRightTriangleButton, dashPentagonButton, dashCircleButton, dashEllipseButton);
        fillShapeMenu.getItems().addAll(fillSquareButton, fillRectButton, fillTriangleButton, fillRightTriangleButton, fillPentagonButton, fillCircleButton, fillEllipseButton);
        polygonMenu.getItems().addAll(polygonButton, dashPolygonButton, fillPolygonButton);
        starMenu.getItems().addAll(starButton, dashStarButton, fillStarButton);

        SideMenuHandles sideMenuHandle = new SideMenuHandles(
                tabPane, toolToggle, shapeToggle, polygonSpinner, starSpinner, drawSettings
        );

        sideMenuHandle.setupActions(
                // Tool buttons
                selectButton, pencilButton, eraserButton, textButton, grabButton,
                polygonButton, dashPolygonButton, fillPolygonButton, starButton,
                dashStarButton, fillStarButton, lineButton, dashButton,

                // Shape buttons
                squareButton, fillSquareButton, dashSquareButton, rectButton,
                fillRectButton, dashRectButton, triangleButton, fillTriangleButton,
                dashTriangleButton, rightTriangleButton, fillRightTriangleButton, dashRightTriangleButton,
                pentagonButton, fillPentagonButton, dashPentagonButton, circleButton,
                fillCircleButton, dashCircleButton, ellipseButton, fillEllipseButton, dashEllipseButton,

                // Utility buttons
                undoButton, redoButton, clearButton,
                rotate90Button, rotate180Button, rotate270Button,
                horiFlipButton, vertFlipButton
        );

    }

    public VBox getView() {return sideMenu;}
}