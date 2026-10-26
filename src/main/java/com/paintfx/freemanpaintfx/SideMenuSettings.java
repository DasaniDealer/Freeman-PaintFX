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

        Label toolsLabel = new Label("Tools");
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

        RadioMenuItem lineButton = new RadioMenuItem("Straight");
        RadioMenuItem dashButton = new RadioMenuItem("Dashed");

        RadioMenuItem squareButton = new RadioMenuItem("Square");
        RadioMenuItem dashSquareButton = new RadioMenuItem("Dashed Square");
        RadioMenuItem rectButton = new RadioMenuItem("Rectangle");
        RadioMenuItem dashRectButton = new RadioMenuItem("Dashed Rectangle");

        RadioMenuItem triangleButton = new RadioMenuItem("Triangle");
        RadioMenuItem dashTriangleButton = new RadioMenuItem("Dashed Triangle");
        RadioMenuItem rightTriangleButton = new RadioMenuItem("Right Triangle");
        RadioMenuItem dashRightTriangleButton = new RadioMenuItem("Dashed Right Triangle");

        RadioMenuItem pentagonButton = new RadioMenuItem("Pentagon");
        RadioMenuItem dashPentagonButton = new RadioMenuItem("Dashed Pentagon");

        RadioMenuItem circleButton = new RadioMenuItem("Circle");
        RadioMenuItem dashCircleButton = new RadioMenuItem("Dashed Circle");
        RadioMenuItem ellipseButton = new RadioMenuItem("Ellipse");
        RadioMenuItem dashEllipseButton = new RadioMenuItem("Dashed Ellipse");

        RadioMenuItem fillSquareButton = new RadioMenuItem("Filled Square");
        RadioMenuItem fillRectButton = new RadioMenuItem("Filled Rectangle");
        RadioMenuItem fillTriangleButton = new RadioMenuItem("Filled Triangle");
        RadioMenuItem fillRightTriangleButton = new RadioMenuItem("Filled Right Triangle");
        RadioMenuItem fillPentagonButton = new RadioMenuItem("Filled Pentagon");
        RadioMenuItem fillCircleButton = new RadioMenuItem("Filled Circle");
        RadioMenuItem fillEllipseButton = new RadioMenuItem("Filled Ellipse");
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
                            false, false, polygonSpinner.getValue());
                } else if (newToggle == dashPolygonButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true, polygonSpinner.getValue());
                } else if (newToggle == fillPolygonButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false, polygonSpinner.getValue());
                } else if (newToggle == starButton) {
                    ShapeHandles.starDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false, starSpinner.getValue());
                } else if (newToggle == dashStarButton) {
                    ShapeHandles.starDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true, starSpinner.getValue());
                } else if (newToggle == fillStarButton) {
                    ShapeHandles.starDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false, starSpinner.getValue());
                } else if (newToggle == lineButton) {
                    SideMenuHandles.drawStraight(context.mainCanvas(), context.prevCanvas(), drawSettings, false);
                } else if (newToggle == dashButton) {
                    SideMenuHandles.drawStraight(context.mainCanvas(), context.prevCanvas(), drawSettings, true);
                }

                isSaved = false;
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
                } else if (newToggle == pentagonButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false, 5);
                } else if (newToggle == fillPentagonButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false, 5);
                } else if (newToggle == dashPentagonButton) {
                    ShapeHandles.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true, 5);
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
                MiscHandles.clearListeners(context);
                SideMenuHandles.clearCanvas(context, event);
            }
        });

        //Rotate
        rotate90Button.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                RotateHandles.handleRotate(context, 90);
            }
        });
        rotate180Button.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                RotateHandles.handleRotate(context, 90);
                RotateHandles.handleRotate(context, 90);
            }
        });
        rotate270Button.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                RotateHandles.handleRotate(context, 270);
            }
        });
        //Flip
        horiFlipButton.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                RotateHandles.handleFlip(context, true, false);
            }
        });
        vertFlipButton.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                RotateHandles.handleFlip(context, false, true);
            }
        });
    }

    public VBox getView() {return sideMenu;}
}