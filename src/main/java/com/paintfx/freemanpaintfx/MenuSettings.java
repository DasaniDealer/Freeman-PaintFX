package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Popup;
import javafx.stage.Stage;


public class MenuSettings extends MenuBar {
    private final int currentSeconds = 60;

    private static WebserverFeature serverInstance;

    public static void setServerInstance(WebserverFeature server) {
        serverInstance = server;
    }

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

        //Options Submenu
        CheckMenuItem toggleAutosave = new CheckMenuItem("Enable Autosave");
        CheckMenuItem shareImage = new CheckMenuItem("Share to Webserver");
        options.getItems().addAll(toggleAutosave, shareImage);

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
                MenuHandles.handleOpen(tabPane, primaryStage);
            }
        });
        openImage.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));

        //Save Image
        saveImage.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                Canvas currentCanvas = context.mainCanvas();

                if (currentCanvas != null) {
                    WritableImage canvasImage = currentCanvas.snapshot(null, null);
                    MenuHandles.handleSave(canvasImage, primaryStage);
                    PaintApplication.isSaved = true; // Note: Handled inside MenuHandles, but safe to keep
                }
            }
        });
        saveImage.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));

        //Save As
        saveAsImage.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                Canvas currentCanvas = context.mainCanvas();

                if (currentCanvas != null) {
                    WritableImage canvasImage = currentCanvas.snapshot(null, null);
                    MenuHandles.handleSaveAs(canvasImage, primaryStage);
                    PaintApplication.isSaved = true; // Note: Handled inside MenuHandles, but safe to keep
                }
            }
        });
        saveAsImage.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));

        //Autosave
        toggleAutosave.selectedProperty().addListener((obs, oldVal, isNowSelected) -> {
            if (isNowSelected) {
                Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
                if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                    AutosaveFeature.startAutosaveTimer(context.mainCanvas(), currentSeconds);
                } else {
                    AutosaveFeature.stopAutosave();
                };
            } else {
                AutosaveFeature.stopAutosave();
            }
        });

        //Share to Webserver
        shareImage.setOnAction(event -> {
            Tab activeTab = tabPane.getSelectionModel().getSelectedItem();
            if (activeTab != null && activeTab.getUserData() instanceof TabFeature.TabRecord context) {
                String tabTitle = activeTab.getText();

                if (shareImage.isSelected()) {
                    TabFeature.sharedCanvases.put(tabTitle, context.mainCanvas());
                } else {
                    TabFeature.sharedCanvases.remove(tabTitle);
                }

                if (serverInstance != null) {
                    serverInstance.updateSelection(TabFeature.sharedCanvases);
                }
            }
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            //Handle Autosave Tab Switching
            if (toggleAutosave.isSelected()) {
                if (newTab != null && newTab.getUserData() instanceof TabFeature.TabRecord context) {
                    AutosaveFeature.startAutosaveTimer(context.mainCanvas(), currentSeconds);
                } else {
                    AutosaveFeature.stopAutosave();
                }
            }

            if (newTab != null) {
                String tabTitle = newTab.getText();
                boolean isShared = TabFeature.sharedCanvases.containsKey(tabTitle);
                shareImage.setSelected(isShared);
            } else {
                shareImage.setSelected(false);
            }
        });

        //Tab Features
        addTab.setOnAction(event -> {
            TabFeature.addTab(tabPane);
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