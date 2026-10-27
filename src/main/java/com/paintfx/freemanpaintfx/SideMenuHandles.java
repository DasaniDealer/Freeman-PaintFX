package com.paintfx.freemanpaintfx;

import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class SideMenuHandles {
    private final TabPane tabPane;
    private final ToggleGroup toolToggle;
    private final ToggleGroup shapeToggle;
    private final Spinner<Integer> polygonSpinner;
    private final Spinner<Integer> starSpinner;
    private final DrawSettings drawSettings;

    private boolean isSaved = true;

    public SideMenuHandles(TabPane tabPane, ToggleGroup toolToggle, ToggleGroup shapeToggle,
                          Spinner<Integer> polygonSpinner, Spinner<Integer> starSpinner, DrawSettings drawSettings) {
        this.tabPane = tabPane;
        this.toolToggle = toolToggle;
        this.shapeToggle = shapeToggle;
        this.polygonSpinner = polygonSpinner;
        this.starSpinner = starSpinner;
        this.drawSettings = drawSettings;
    }

    public void setupActions(
            // Tool Buttons
            Object selectButton, Object pencilButton, Object eraserButton, Object textButton,
            Object grabButton, Object polygonButton, Object dashPolygonButton, Object fillPolygonButton,
            Object starButton, Object dashStarButton, Object fillStarButton, Object lineButton, Object dashButton,
            // Shape Buttons
            Object squareButton, Object fillSquareButton, Object dashSquareButton, Object rectButton,
            Object fillRectButton, Object dashRectButton, Object triangleButton, Object fillTriangleButton,
            Object dashTriangleButton, Object rightTriangleButton, Object fillRightTriangleButton, Object dashRightTriangleButton,
            Object pentagonButton, Object fillPentagonButton, Object dashPentagonButton, Object circleButton,
            Object fillCircleButton, Object dashCircleButton, Object ellipseButton, Object fillEllipseButton, Object dashEllipseButton,
            // Utility Buttons
            Button undoButton, Button redoButton, Button clearButton,
            Button rotate90Button, Button rotate180Button, Button rotate270Button,
            Button horiFlipButton, Button vertFlipButton) {

        //Tool Buttons
        toolToggle.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                MiscFeatures.clearListeners(context);
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
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Select Selected");
                } else if (newToggle == pencilButton) {
                    SideMenuFeatures.drawLine(context.canvasContainer(), drawSettings, context.gc());
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Pencil Selected");
                } else if (newToggle == eraserButton) {
                    SideMenuFeatures.eraserTool(context.canvasContainer(), drawSettings, context.gc());
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Eraser Selected");
                } else if (newToggle == textButton) {
                    SideMenuFeatures.textTool(context.canvasContainer(), drawSettings, context.gc(), toolToggle);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Text Selected");
                } else if (newToggle == grabButton) {
                    SideMenuFeatures.grabColor(context.canvasContainer(), drawSettings, toolToggle);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Color Grabber Selected");
                } else if (newToggle == polygonButton) {
                    ShapeFeatures.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false, polygonSpinner.getValue());
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Polygon Selected");
                } else if (newToggle == dashPolygonButton) {
                    ShapeFeatures.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true, polygonSpinner.getValue());
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Dashed Polygon Selected");
                } else if (newToggle == fillPolygonButton) {
                    ShapeFeatures.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false, polygonSpinner.getValue());
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Filled Polygon Selected");
                } else if (newToggle == starButton) {
                    ShapeFeatures.starDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false, starSpinner.getValue());
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Star Selected");
                } else if (newToggle == dashStarButton) {
                    ShapeFeatures.starDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true, starSpinner.getValue());
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Dashed Star Selected");
                } else if (newToggle == fillStarButton) {
                    ShapeFeatures.starDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false, starSpinner.getValue());
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Filled Star Selected");
                } else if (newToggle == lineButton) {
                    SideMenuFeatures.drawStraight(context.mainCanvas(), context.prevCanvas(), drawSettings, false);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Line Selected");
                } else if (newToggle == dashButton) {
                    SideMenuFeatures.drawStraight(context.mainCanvas(), context.prevCanvas(), drawSettings, true);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Dashed Line Selected");
                }
                isSaved = false;
            }
        });

        //Shapes
        shapeToggle.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                MiscFeatures.clearListeners(context);
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
                    ShapeFeatures.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false, 4);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Square Selected");
                } else if (newToggle == fillSquareButton) {
                    ShapeFeatures.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false, 4);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Filled Square Selected");
                } else if (newToggle == dashSquareButton) {
                    ShapeFeatures.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true, 4);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Dashed Square Selected");
                } else if (newToggle == rectButton) {
                    ShapeFeatures.rectDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Rectangle Selected");
                } else if (newToggle == fillRectButton) {
                    ShapeFeatures.rectDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Filled Rectangle Selected");
                } else if (newToggle == dashRectButton) {
                    ShapeFeatures.rectDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Dashed Rectangle Selected");
                } else if (newToggle == triangleButton) {
                    ShapeFeatures.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false, 3);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Triangle Selected");
                } else if (newToggle == fillTriangleButton) {
                    ShapeFeatures.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false, 3);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Filled Triangle Selected");
                } else if (newToggle == dashTriangleButton) {
                    ShapeFeatures.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true, 3);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Dashed Triangle Selected");
                } else if (newToggle == rightTriangleButton) {
                    ShapeFeatures.rightTriangleDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Right Triangle Selected");
                } else if (newToggle == fillRightTriangleButton) {
                    ShapeFeatures.rightTriangleDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Filled Right Triangle Selected");
                } else if (newToggle == dashRightTriangleButton) {
                    ShapeFeatures.rightTriangleDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Dashed Right Triangle Selected");
                } else if (newToggle == pentagonButton) {
                    ShapeFeatures.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false, 5);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Pentagon Selected");
                } else if (newToggle == fillPentagonButton) {
                    ShapeFeatures.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false, 5);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Filled Pentagon Selected");
                } else if (newToggle == dashPentagonButton) {
                    ShapeFeatures.polygonDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true, 5);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Dashed Pentagon Selected");
                } else if (newToggle == circleButton) {
                    ShapeFeatures.circleDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Circle Selected");
                } else if (newToggle == fillCircleButton) {
                    ShapeFeatures.circleDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Filled Circle Selected");
                } else if (newToggle == dashCircleButton) {
                    ShapeFeatures.circleDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Dashed Circle Selected");
                } else if (newToggle == ellipseButton) {
                    ShapeFeatures.ellipseDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, false);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Ellipse Selected");
                } else if (newToggle == fillEllipseButton) {
                    ShapeFeatures.ellipseDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            true, false);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Filled Ellipse Selected");
                } else if (newToggle == dashEllipseButton) {
                    ShapeFeatures.ellipseDraw(context.mainCanvas(), context.prevCanvas(), drawSettings,
                            false, true);
                    PaintLogger.getInstance().logHandle("SideMenuSettings","Dashed Ellipse Selected");
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
                MiscFeatures.clearListeners(context);
                SideMenuFeatures.clearCanvas(context, event);
                PaintLogger.getInstance().logHandle("SideMenuSettings","Canvas Cleared");
            }
        });

        //Rotate
        rotate90Button.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                RotateFeatures.handleRotate(context, 90);
                PaintLogger.getInstance().logHandle("SideMenuSettings","Rotated 90 Degrees");
            }
        });
        rotate180Button.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                RotateFeatures.handleRotate(context, 90);
                RotateFeatures.handleRotate(context, 90);
                PaintLogger.getInstance().logHandle("SideMenuSettings","Rotated 180 Degrees");
            }
        });
        rotate270Button.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                RotateFeatures.handleRotate(context, 90);
                RotateFeatures.handleRotate(context, 90);
                RotateFeatures.handleRotate(context, 90);
                PaintLogger.getInstance().logHandle("SideMenuSettings","Rotated 270 Degrees");
            }
        });

        //Flip
        horiFlipButton.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                RotateFeatures.handleFlip(context, true, false);
                PaintLogger.getInstance().logHandle("SideMenuSettings","Horizontal Flip");
            }
        });
        vertFlipButton.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                RotateFeatures.handleFlip(context, false, true);
                PaintLogger.getInstance().logHandle("SideMenuSettings","Vertical Flip");
            }
        });
    }
}
