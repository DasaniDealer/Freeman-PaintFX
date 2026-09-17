package com.paintfx.freemanpaintfx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import java.util.Optional;

public class CloseInterceptFeature {
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
