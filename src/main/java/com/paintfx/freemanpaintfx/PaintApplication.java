package com.paintfx.freemanpaintfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PaintApplication extends Application {
    static boolean isSaved = false;

    /**
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        primaryStage.setTitle("(Pain)t");

        //MAIN LAYOUT
        DrawSettings drawSettings = new DrawSettings();
        TabPane tabPane = new TabPane();

        MenuSettings menuBar = new MenuSettings(primaryStage, tabPane, drawSettings);
        BorderPane root = new BorderPane();
        SideMenuSettings sideMenu = new SideMenuSettings(tabPane, drawSettings);

        root.setTop(menuBar);
        root.setRight(sideMenu.getView());
        root.setCenter(tabPane);

        //Tab Add
        TabFeature.addTab(tabPane);

        //Webserver Add
        WebserverFeature webserver = new WebserverFeature();
        try {
            webserver.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MenuSettings.setServerInstance(webserver);

        //SHOWTIME
        Scene scene = new Scene(root, 1150, 650);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void stop() {
        //Clean up the timer when the application closes
        if (AutosaveFeature.autosaveTimeline != null) {
            AutosaveFeature.stopAutosave();
        }
        //Close System on Exit
        javafx.application.Platform.exit();
        System.exit(0);
    }

    /**
     * @param args Main execution method
     */
    public static void main(String[] args) {
        launch(args);
    }
}