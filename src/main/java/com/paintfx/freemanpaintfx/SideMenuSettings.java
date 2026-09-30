package com.paintfx.freemanpaintfx;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class SideMenuSettings {
    private final VBox sideMenu;
    private boolean isSaved;

    public SideMenuSettings(TabPane tabPane, DrawSettings drawSettings) {
        sideMenu = new VBox(15);
        sideMenu.setStyle("-fx-padding: 15; -fx-background-color: #c5c7ca; -fx-pref-width: 160;");

        ToggleButton grabButton = drawSettings.getGrabButton();

        Label toolsLabel = new Label("Tools");
        toolsLabel.setStyle("-fx-font-weight: bold;");
        ToggleButton pencilButton = new ToggleButton("Pencil");
        ToggleButton eraserButton = new ToggleButton("Eraser");
        Button clearButton = new Button("Clear Canvas");

        RadioMenuItem lineButton = new RadioMenuItem("Line");
        RadioMenuItem dashButton = new RadioMenuItem("Dashed");

        RadioMenuItem squareButton = new RadioMenuItem("Square");
        RadioMenuItem dashSquareButton = new RadioMenuItem("Dashed Square");
        RadioMenuItem rectButton = new RadioMenuItem("Rectangle");
        RadioMenuItem dashRectButton = new RadioMenuItem("Dashed Rectangle");

        RadioMenuItem triangleButton = new RadioMenuItem("Triangle");
        RadioMenuItem dashTriangleButton = new RadioMenuItem("Dashed Triangle");

        RadioMenuItem circleButton = new RadioMenuItem("Circle");
        RadioMenuItem dashCircleButton = new RadioMenuItem("Dashed Circle");
        RadioMenuItem ellipseButton = new RadioMenuItem("Ellipse");
        RadioMenuItem dashEllipseButton = new RadioMenuItem("Dashed Ellipse");

        RadioMenuItem fillSquareButton = new RadioMenuItem("Filled Square");
        RadioMenuItem fillRectButton = new RadioMenuItem("Filled Rectangle");
        RadioMenuItem fillTriangleButton = new RadioMenuItem("Filled Triangle");
        RadioMenuItem fillCircleButton = new RadioMenuItem("Filled Circle");
        RadioMenuItem fillEllipseButton = new RadioMenuItem("Filled Ellipse");

        //Menu Groups
        MenuButton lineMenu = new MenuButton("Line");
        MenuButton shapeMenu = new MenuButton("Shape");
        MenuButton dashMenu = new MenuButton("Dashed Shape");
        MenuButton fillShapeMenu = new MenuButton("Filled Shape");

        //Toggle Groups
        ToggleGroup toolToggle = new ToggleGroup();
        ToggleGroup lineToggle = new ToggleGroup();
        ToggleGroup shapeToggle = new ToggleGroup();

        toolToggle.getToggles().addAll(pencilButton, eraserButton, grabButton);
        lineToggle.getToggles().addAll(lineButton, dashButton);
        //Shape Menu
        shapeToggle.getToggles().addAll(squareButton, dashSquareButton, fillSquareButton,
                rectButton, dashRectButton, fillRectButton,
                triangleButton, dashTriangleButton, fillTriangleButton,
                circleButton, dashCircleButton, fillCircleButton,
                ellipseButton, dashEllipseButton, fillEllipseButton);

        //Clear New Tab Listeners
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                //Clear Toggles
                toolToggle.selectToggle(null);
                lineToggle.selectToggle(null);
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
                toolsLabel, pencilButton, eraserButton, clearButton,
                lineMenu, shapeMenu, dashMenu, fillShapeMenu,
                drawSettings
        );

        lineMenu.getItems().addAll(lineButton, dashButton);
        shapeMenu.getItems().addAll(squareButton, rectButton, triangleButton, circleButton, ellipseButton);
        dashMenu.getItems().addAll(dashSquareButton, dashRectButton, dashTriangleButton, dashCircleButton, dashEllipseButton);
        fillShapeMenu.getItems().addAll(fillSquareButton, fillRectButton, fillTriangleButton, fillCircleButton, fillEllipseButton);

        //Free Draw/Erase
        toolToggle.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                MiscHandles.clearListeners(context);
                if (newToggle == null) {return;}

                //Unselect Dropdown Items
                lineToggle.selectToggle(null);
                shapeToggle.selectToggle(null);

                //Toggle Button Effects
                if (newToggle == pencilButton) {
                    SideMenuHandles.drawLine(context.canvasContainer(), drawSettings, context.gc());
                }
                if (newToggle == eraserButton) {
                    SideMenuHandles.eraserTool(context.canvasContainer(), drawSettings, context.gc());
                }
                if (newToggle == grabButton) {
                    SideMenuHandles.grabColor(context.canvasContainer(), drawSettings, toolToggle);
                }

                isSaved = false;
            }
        });

        //Lines
        lineToggle.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                //Clear Canvas Mouse
                MiscHandles.clearListeners(context);
                if (newToggle == null) return;

                //Unselect Main Menu Items
                toolToggle.selectToggle(null);
                shapeToggle.selectToggle(null);
                //Toggle Button Effects
                if (newToggle == lineButton) {
                    SideMenuHandles.drawStraight(context.mainCanvas(), context.prevCanvas(), drawSettings, false);
                } else if (newToggle == dashButton) {
                    SideMenuHandles.drawStraight(context.mainCanvas(), context.prevCanvas(), drawSettings, true);
                }

                isSaved = false;
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

                //Unselect Main Menu Items
                toolToggle.selectToggle(null);
                lineToggle.selectToggle(null);
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