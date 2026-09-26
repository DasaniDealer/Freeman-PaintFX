package com.paintfx.freemanpaintfx;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class SideMenuSettings {
    private VBox sideMenu;
    private boolean isSaved;

    public SideMenuSettings(TabPane tabPane, DrawSettings drawSettings) {
        sideMenu = new VBox(15);
        sideMenu.setStyle("-fx-padding: 15; -fx-background-color: #c5c7ca; -fx-pref-width: 160;");

        ToggleButton grabButton = drawSettings.getGrabButton();

        Label toolsLabel = new Label("Tools");
        toolsLabel.setStyle("-fx-font-weight: bold;");
        ToggleButton pencilButton = new ToggleButton("Pencil");
        ToggleButton eraserButton = new ToggleButton("Eraser");

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
                toolsLabel, pencilButton, lineMenu, shapeMenu, dashMenu, fillShapeMenu, eraserButton,
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
                context.canvasContainer().setOnMousePressed(null);
                context.canvasContainer().setOnMouseDragged(null);
                context.canvasContainer().setOnMouseReleased(null);

                if (newToggle == null) {
                    return;
                }

                //Unselect Dropdown Items
                lineToggle.selectToggle(null);
                shapeToggle.selectToggle(null);

                //Toggle Button Effects
                if (newToggle == pencilButton) {
                    CanvasFeature.drawLine(context.canvasContainer(), drawSettings);
                }

                if (newToggle == grabButton) {
                    CanvasFeature.grabColor(context.canvasContainer(), drawSettings, toolToggle);
                }

                isSaved = false;
            }
        });
        //Lines
        lineToggle.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                context.canvasContainer().setOnMousePressed(null);
                context.canvasContainer().setOnMouseDragged(null);
                context.canvasContainer().setOnMouseReleased(null);

                if (newToggle == null) return;

                //Unselect Main Menu Items
                toolToggle.selectToggle(null);
                shapeToggle.selectToggle(null);
                //Toggle Button Effects
                if (newToggle == lineButton) {
                    CanvasFeature.drawStraight(context.canvasContainer(), drawSettings, false);
                } else if (newToggle == dashButton) {
                    CanvasFeature.drawStraight(context.canvasContainer(), drawSettings, true);
                }

                isSaved = false;
            }
        });
        //Shapes
        shapeToggle.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                context.canvasContainer().setOnMousePressed(null);
                context.canvasContainer().setOnMouseDragged(null);
                context.canvasContainer().setOnMouseReleased(null);

                if (newToggle == null) return;

                //Unselect Main Menu Items
                toolToggle.selectToggle(null);
                lineToggle.selectToggle(null);
                //region Toggle Button Effects
                if (newToggle == squareButton) {
                    ShapeFeature.rectDraw(context.canvasContainer(), drawSettings, false, false, true);
                } else if (newToggle == fillSquareButton) {
                    ShapeFeature.rectDraw(context.canvasContainer(), drawSettings, true, false, true);
                } else if (newToggle == dashSquareButton) {
                    ShapeFeature.rectDraw(context.canvasContainer(), drawSettings, false, true, true);
                }

                if (newToggle == rectButton) {
                    ShapeFeature.rectDraw(context.canvasContainer(), drawSettings, false, false, false);
                } else if (newToggle == fillRectButton) {
                    ShapeFeature.rectDraw(context.canvasContainer(), drawSettings, true, false, false);
                } else if (newToggle == dashRectButton) {
                    ShapeFeature.rectDraw(context.canvasContainer(), drawSettings, false, true, false);
                } else if (newToggle == triangleButton) {
                    ShapeFeature.triangleDraw(context.canvasContainer(), drawSettings, false, false);
                } else if (newToggle == fillTriangleButton) {
                    ShapeFeature.triangleDraw(context.canvasContainer(), drawSettings, true, false);
                } else if (newToggle == dashTriangleButton) {
                    ShapeFeature.triangleDraw(context.canvasContainer(), drawSettings, false, true);
                } else if (newToggle == circleButton) {
                    ShapeFeature.circleDraw(context.canvasContainer(), drawSettings, false, false);
                } else if (newToggle == fillCircleButton) {
                    ShapeFeature.circleDraw(context.canvasContainer(), drawSettings, true, false);
                } else if (newToggle == dashCircleButton) {
                    ShapeFeature.circleDraw(context.canvasContainer(), drawSettings, false, true);
                } else if (newToggle == ellipseButton) {
                    ShapeFeature.ellipseDraw(context.canvasContainer(), drawSettings, false, false);
                } else if (newToggle == fillEllipseButton) {
                    ShapeFeature.ellipseDraw(context.canvasContainer(), drawSettings, true, false);
                } else if (newToggle == dashEllipseButton) {
                    ShapeFeature.ellipseDraw(context.canvasContainer(), drawSettings, false, true);
                }
                //endregion
                isSaved = false;
            }
        });
    }

    public VBox getView() {return sideMenu;}
}