package com.paintfx.freemanpaintfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
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

        SideMenuSettings sideMenu = new SideMenuSettings(tabPane, drawSettings);

        root.setTop(menuBar);
        root.setRight(sideMenu.getView());
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