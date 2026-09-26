package com.paintfx.freemanpaintfx;

import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Popup;
import javafx.stage.Stage;


public class MenuSettings extends MenuBar{
    public MenuSettings(Stage primaryStage, TabPane tabPane, DrawSettings drawSettings) {
        //Menu Bar
        Menu file = new Menu("File");
        Menu options = new Menu("Options");
        Menu tabs = new Menu("Tabs");
        Menu help = new Menu("Help");

        this.getMenus().addAll(file, options, tabs, help);

        //File Submenu
        MenuItem openImage = new MenuItem("Open");
        MenuItem saveImage = new MenuItem("Save");
        MenuItem saveAsImage = new MenuItem("Save As");

        file.getItems().addAll(openImage, saveImage, saveAsImage);

        //Tabs Submenu
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
                PaintApplication.isSaved = true;
            }
        });
        saveImage.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));

        //Save As
        saveAsImage.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                WritableImage combineImage = context.combineArea().snapshot(null, null);
                SaveFeature.saveAs(combineImage, primaryStage);
                PaintApplication.isSaved = true;
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

            MenuHandles.handleHelp(helpPopup, primaryStage);
        });

        //Close Intercept
        primaryStage.setOnCloseRequest(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                WritableImage combineImage = context.combineArea().snapshot(null, null);

                MenuHandles.handleExit(event, combineImage);
            }
        });
    }
}