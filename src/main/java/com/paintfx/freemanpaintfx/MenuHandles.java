package com.paintfx.freemanpaintfx;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import java.util.Optional;

public class MenuHandles {
    public static void handleHelp(Popup helpPopup, Stage primaryStage) {
        VBox popupRoot = new VBox(15);
        popupRoot.setAlignment(Pos.CENTER);
        popupRoot.setStyle("-fx-padding: 15; " + "-fx-background-color: #D3D3D3;");

        Label titleLabel = new Label("Freeman PaintFX v4.0.0");
        titleLabel.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 24px;");

        Label descriptionLabel = new Label("Previous Versions: https://github.com/DasaniDealer/Freeman-PaintFX");
        descriptionLabel.setStyle("-fx-font-size: 14px;");

        descriptionLabel.setWrapText(true);

        //Close Button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> helpPopup.hide());

        //Layout
        popupRoot.getChildren().addAll(titleLabel, descriptionLabel, closeButton);

        helpPopup.getContent().add(popupRoot);

        helpPopup.show(primaryStage);
    }

    public static void handleExit(WindowEvent event, WritableImage imageSnap) {
        if(PaintApplication.isSaved) {
            return;
        }

        //Confirm Dialog
        Alert closeAlert = new Alert(Alert.AlertType.CONFIRMATION);
        closeAlert.setTitle("You Are About To Exit Without Saving");
        closeAlert.setContentText("Save Before Exiting?");

        ButtonType buttonSave = new ButtonType("Save");
        ButtonType buttonDiscard = new ButtonType("Discard");
        ButtonType buttonCancel = new ButtonType("Cancel");

        closeAlert.getButtonTypes().setAll(buttonSave, buttonDiscard, buttonCancel);

        //Button Choice
        Optional<ButtonType> result = closeAlert.showAndWait();

        if(result.isPresent()) {
            if(result.get() == buttonSave) {
                Window windowSnap = (Window) event.getTarget();
                SaveFeature.saveAs(imageSnap, windowSnap);
            }
            else if(result.get() == buttonDiscard) {
                return;
            }
            else {
                event.consume();
            }
        }
    }
}