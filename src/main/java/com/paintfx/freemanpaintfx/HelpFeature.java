package com.paintfx.freemanpaintfx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class HelpFeature {
    public static void handleHelp(Popup helpPopup, Stage primaryStage) {
        VBox popupRoot = new VBox(15);
        popupRoot.setAlignment(Pos.CENTER);
        popupRoot.setStyle("-fx-padding: 15; " + "-fx-background-color: #D3D3D3;");

        Label titleLabel = new Label("Freeman PaintFX v2.6.0");
        titleLabel.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 24px;");

        Label descriptionLabel = new Label("Previous Versions: https://github.com/DasaniDealer/Freeman-PaintFX");
        descriptionLabel.setStyle("-fx-font-size: 14px;");

        descriptionLabel.setWrapText(true);

        //Close Button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> helpPopup.hide());

        //Layout
        popupRoot.getChildren().addAll(titleLabel, descriptionLabel, closeButton);

        helpPopup.getContent().add(popupRoot);

        helpPopup.show(primaryStage);
    }
}
