package com.paintfx.freemanpaintfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;

public class PaintApplication extends Application {
    static boolean isSaved = false;

    @Override
    public void start(Stage primaryStage) throws IOException {

        primaryStage.setTitle("(Pain)t");

        DrawSettings drawSettings = new DrawSettings();
        ToggleButton grabButton = drawSettings.getGrabButton();

        //Menu Bar
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu("File");
        Menu options = new Menu("Options");
        Menu tabs = new Menu("Tabs");
        Menu help = new Menu("Help");

        menuBar.getMenus().addAll(file, options, tabs, help);

        //File Submenu
        MenuItem openImage = new MenuItem("Open");
        MenuItem saveImage = new MenuItem("Save");
        MenuItem saveAsImage = new MenuItem("Save As");

        file.getItems().addAll(openImage, saveImage, saveAsImage);

        //Tabs Submenu
        TabPane tabPane = new TabPane();

        MenuItem addTab = new MenuItem("Add Tab");
        tabs.getItems().addAll(addTab);

        //Help Submenu
        MenuItem helpAct = new MenuItem("About Paint");
        help.getItems().addAll(helpAct);

        //region Side Menu
        VBox sideMenu = new VBox(15);
        sideMenu.setStyle("-fx-padding: 15; -fx-background-color: #c5c7ca; -fx-pref-width: 160;");

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

        //Side Menu Placements
        sideMenu.getChildren().addAll(
                toolsLabel, pencilButton, lineMenu, shapeMenu, dashMenu, fillShapeMenu, eraserButton,
                drawSettings
        );
        //endregion

        lineMenu.getItems().addAll(lineButton, dashButton);
        shapeMenu.getItems().addAll(squareButton, rectButton, triangleButton, circleButton, ellipseButton);
        dashMenu.getItems().addAll(dashSquareButton, dashRectButton, dashTriangleButton, dashCircleButton, dashEllipseButton);
        fillShapeMenu.getItems().addAll(fillSquareButton, fillRectButton, fillTriangleButton, fillCircleButton, fillEllipseButton);

        //Open Image
        openImage.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                OpenFeature.open(context.imageView(), primaryStage, context.gc(), context.canvas(), context.canvasContainer());
            }
        });
        openImage.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));

        //Save Image
        saveImage.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                WritableImage combineImage = context.combineArea().snapshot(null, null);
                SaveFeature.save(combineImage, primaryStage);
                isSaved = true;
            }
        });
        saveImage.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));

        //Save As
        saveAsImage.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                WritableImage combineImage = context.combineArea().snapshot(null, null);
                SaveFeature.saveAs(combineImage, primaryStage);
                isSaved = true;
            }
        });
        saveAsImage.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));

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

        //Tab Features
        addTab.setOnAction(event -> {
            TabFeature.addTab(tabPane, drawSettings);
        });

        //Help Popup
        helpAct.setOnAction(event -> {
            Popup helpPopup = new Popup();

            HelpFeature.handleHelp(helpPopup, primaryStage);
        });

        //Close Intercept
        primaryStage.setOnCloseRequest(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                WritableImage combineImage = context.combineArea().snapshot(null, null);

                CloseInterceptFeature.handleExit(event, combineImage);
            }
        });

        //MAIN LAYOUT
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setRight(sideMenu);
        root.setCenter(tabPane);

        TabFeature.addTab(tabPane, drawSettings);

        //SHOWTIME
        Scene scene = new Scene(root, 1150, 650);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}