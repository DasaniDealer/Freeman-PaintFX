package com.paintfx.freemanpaintfx;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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

        Label toolsLabel = new Label("Tools");
        toolsLabel.setStyle("-fx-font-weight: bold;");

        //Undo Redo Buttons
        Button undoButton = new Button("Undo");
        Button redoButton = new Button("Redo");

        undoButton.setOnAction(event -> triggerAction(tabPane, context -> CanvasHistory.undo(context.mainCanvas())));
        redoButton.setOnAction(event -> triggerAction(tabPane, context -> CanvasHistory.redo(context.mainCanvas())));

        ToggleButton selectButton = new ToggleButton("Select");
        ToggleButton pencilButton = new ToggleButton("Pencil");
        ToggleButton eraserButton = new ToggleButton("Eraser");
        ToggleButton textButton = new ToggleButton("Text");
        ToggleButton grabButton = drawSettings.getGrabButton();
        Button clearButton = new Button("Clear Canvas");


        RadioMenuItem lineButton = new RadioMenuItem("Line");
        RadioMenuItem dashButton = new RadioMenuItem("Dashed");

        RadioMenuItem squareButton = new RadioMenuItem("Square");
        RadioMenuItem dashSquareButton = new RadioMenuItem("Dashed Square");
        RadioMenuItem rectButton = new RadioMenuItem("Rectangle");
        RadioMenuItem dashRectButton = new RadioMenuItem("Dashed Rectangle");

        RadioMenuItem triangleButton = new RadioMenuItem("Triangle");
        RadioMenuItem dashTriangleButton = new RadioMenuItem("Dashed Triangle");
        RadioMenuItem rightTriangleButton = new RadioMenuItem("Right Triangle");
        RadioMenuItem dashRightTriangleButton = new RadioMenuItem("Dashed Right Triangle");

        RadioMenuItem circleButton = new RadioMenuItem("Circle");
        RadioMenuItem dashCircleButton = new RadioMenuItem("Dashed Circle");
        RadioMenuItem ellipseButton = new RadioMenuItem("Ellipse");
        RadioMenuItem dashEllipseButton = new RadioMenuItem("Dashed Ellipse");

        RadioMenuItem fillSquareButton = new RadioMenuItem("Filled Square");
        RadioMenuItem fillRectButton = new RadioMenuItem("Filled Rectangle");
        RadioMenuItem fillTriangleButton = new RadioMenuItem("Filled Triangle");
        RadioMenuItem fillRightTriangleButton = new RadioMenuItem("Filled Right Triangle");
        RadioMenuItem fillCircleButton = new RadioMenuItem("Filled Circle");
        RadioMenuItem fillEllipseButton = new RadioMenuItem("Filled Ellipse");
        //Custom Polygon Button
        RadioMenuItem polygonButton = new RadioMenuItem("Polygon");
        RadioMenuItem dashPolygonButton = new RadioMenuItem("Dashed Polygon");
        RadioMenuItem fillPolygonButton = new RadioMenuItem("Filled Polygon");
        Spinner<Integer> sideSpinner = new Spinner<>(3, 20, 5);
        sideSpinner.setEditable(true);

        //Menu Groups
        MenuButton lineMenu = new MenuButton("Line");
        MenuButton shapeMenu = new MenuButton("Shape");
        MenuButton dashMenu = new MenuButton("Dashed Shape");
        MenuButton fillShapeMenu = new MenuButton("Filled Shape");
        MenuButton polygonMenu = new MenuButton("Polygon");

        //Toggle Groups
        ToggleGroup toolToggle = new ToggleGroup();
        ToggleGroup shapeToggle = new ToggleGroup();

        toolToggle.getToggles().addAll(selectButton, pencilButton, eraserButton,
                textButton, grabButton,
                polygonButton, dashPolygonButton, fillPolygonButton,
                lineButton, dashButton);

        //Shape Menu
        shapeToggle.getToggles().addAll(squareButton, dashSquareButton, fillSquareButton,
                rectButton, dashRectButton, fillRectButton,
                triangleButton, dashTriangleButton, fillTriangleButton,
                rightTriangleButton, dashRightTriangleButton, fillRightTriangleButton,
                circleButton, dashCircleButton, fillCircleButton,
                ellipseButton, dashEllipseButton, fillEllipseButton);

        //Polygon HBox
        HBox polygonBox = new HBox(5);
        polygonBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        sideSpinner.setPrefWidth(75);
        polygonMenu.setPrefWidth(110);

        polygonBox.getChildren().addAll(sideSpinner, polygonMenu);

        //Undo Redo HBox
        HBox undoRedoBox = new HBox(10);
        undoRedoBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        undoRedoBox.getChildren().addAll(undoButton, redoButton);

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
                toolsLabel, undoRedoBox,
                selectButton, pencilButton, eraserButton, textButton, clearButton,
                lineMenu, shapeMenu, dashMenu, fillShapeMenu,
                polygonBox, drawSettings
        );

        lineMenu.getItems().addAll(lineButton, dashButton);
        shapeMenu.getItems().addAll(squareButton, rectButton, triangleButton, rightTriangleButton, circleButton, ellipseButton);
        dashMenu.getItems().addAll(dashSquareButton, dashRectButton, dashTriangleButton, dashRightTriangleButton, dashCircleButton, dashEllipseButton);
        fillShapeMenu.getItems().addAll(fillSquareButton, fillRectButton, fillTriangleButton, fillRightTriangleButton, fillCircleButton, fillEllipseButton);
        polygonMenu.getItems().addAll(polygonButton, dashPolygonButton, fillPolygonButton);

        //Tool Buttons
        toolToggle.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                MiscHandles.clearListeners(context);
                if (newToggle == null) {return;}

                context.canvasContainer().addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
                    //Save State on Left-Click
                    if (event.isPrimaryButtonDown()) {
                        CanvasHistory.saveState(context.mainCanvas());
                    }
                });

                //Unselect Dropdown Items
                shapeToggle.selectToggle(null);

                //Toggle Button Effects
                if (newToggle == selectButton) {
                    SelectSettings.selectionTool(context.mainCanvas(), context.prevCanvas());
                } else if (newToggle == pencilButton) {
                    SideMenuHandles.drawLine(context.canvasContainer(), drawSettings, context.gc());
                } else if (newToggle == eraserButton) {
                    SideMenuHandles.eraserTool(context.canvasContainer(), drawSettings, context.gc());
                } else if (newToggle == textButton) {
                    SideMenuHandles.textTool(context.canvasContainer(), drawSettings, context.gc(), toolToggle);
                } else if (newToggle == grabButton) {
                    SideMenuHandles.grabColor(context.canvasContainer(), drawSettings, toolToggle);
                } else if (newToggle == polygonButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false, sideSpinner.getValue());
                } else if (newToggle == dashPolygonButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true, sideSpinner.getValue());
                } else if (newToggle == fillPolygonButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false, sideSpinner.getValue());
                } else if (newToggle == lineButton) {
                    SideMenuHandles.drawStraight(context.mainCanvas(), context.prevCanvas(), drawSettings, false);
                } else if (newToggle == dashButton) {
                    SideMenuHandles.drawStraight(context.mainCanvas(), context.prevCanvas(), drawSettings, true);
                }

                isSaved = false;
            }
        });

        //Undo Redo Button
        undoButton.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                CanvasHistory.undo(context.mainCanvas());
            }
        });
        redoButton.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                CanvasHistory.redo(context.mainCanvas());
            }
        });

        //Clear Button
        clearButton.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                SideMenuHandles.clearCanvas(context, event);
            }
        });

        //Shapes
        shapeToggle.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                MiscHandles.clearListeners(context);
                if (newToggle == null) return;

                context.canvasContainer().addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
                    //Save State on Left-Click
                    if (event.isPrimaryButtonDown()) {
                        CanvasHistory.saveState(context.mainCanvas());
                    }
                });

                //Unselect Main Menu Items
                toolToggle.selectToggle(null);
                //Toggle Button Effects
                if (newToggle == squareButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false, 4);
                } else if (newToggle == fillSquareButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false, 4);
                } else if (newToggle == dashSquareButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true, 4);
                } else if (newToggle == rectButton) {
                    ShapeHandles.rectDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false);
                } else if (newToggle == fillRectButton) {
                    ShapeHandles.rectDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false);
                } else if (newToggle == dashRectButton) {
                    ShapeHandles.rectDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true);
                } else if (newToggle == triangleButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false, 3);
                } else if (newToggle == fillTriangleButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false, 3);
                } else if (newToggle == dashTriangleButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true, 3);
                } else if (newToggle == rightTriangleButton) {
                    ShapeHandles.rightTriangleDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false);
                } else if (newToggle == fillRightTriangleButton) {
                    ShapeHandles.rightTriangleDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false);
                } else if (newToggle == dashRightTriangleButton) {
                    ShapeHandles.rightTriangleDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true);
                } else if (newToggle == circleButton) {
                    ShapeHandles.circleDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false);
                } else if (newToggle == fillCircleButton) {
                    ShapeHandles.circleDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false);
                } else if (newToggle == dashCircleButton) {
                    ShapeHandles.circleDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true);
                } else if (newToggle == ellipseButton) {
                    ShapeHandles.ellipseDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false);
                } else if (newToggle == fillEllipseButton) {
                    ShapeHandles.ellipseDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false);
                } else if (newToggle == dashEllipseButton) {
                    ShapeHandles.ellipseDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true);
                }
                isSaved = false;
            }
        });
    }

    public VBox getView() {return sideMenu;}
}