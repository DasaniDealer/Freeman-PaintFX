package com.paintfx.freemanpaintfx;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MenuHandles {
    private static File currentFile = null;

    //Open Function
    public static void handleOpen(TabPane tabPane, Stage primaryStage) {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab == null) return;
        TabFeature.TabRecord record = (TabFeature.TabRecord) currentTab.getUserData();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        File imageFile = fileChooser.showOpenDialog(primaryStage);
        if (imageFile != null) {
            try {
                Image image = new Image(imageFile.toURI().toString());

                double width = image.getWidth();
                double height = image.getHeight();

                record.imageView().setImage(image);
                record.mainCanvas().setWidth(width);
                record.mainCanvas().setHeight(height);
                record.prevCanvas().setWidth(width);
                record.prevCanvas().setHeight(height);

                record.gc().clearRect(0, 0, width, height);
                record.pgc().clearRect(0, 0, width, height);

                CanvasHistory.clearHistory();
            } catch (Exception error) {
                System.err.println("Error loading image file: " + error.getMessage());
                error.printStackTrace();
            }
        }
    }
    //Save Function
    public static void handleSave(Image image, Window primaryWindow) {
        if (image == null) {
            return;
        }
        //Save
        if (currentFile != null) {
            try {
                String fileName = currentFile.getName();
                String path = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), path, currentFile);
                System.out.println("Saved " + currentFile.getAbsolutePath());
            } catch (IOException e) {
                handleSaveAs(image, primaryWindow);
            }
        }
        //Save As
        else {
            handleSaveAs(image, primaryWindow);
        }
    }
    //Save As Function
    public static void handleSaveAs(Image image, Window primaryWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files (*.png)", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Files (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("GIF Files (*.gif)", "*.gif"),
                new FileChooser.ExtensionFilter("BMP Files (*.bmp)", "*.bmp" )
        );

        File saveFile = fileChooser.showSaveDialog(primaryWindow);

        if (saveFile != null) {
            try {
                String fileName = saveFile.getName();
                String fileExtension = "png"; //default

                //Find Extension
                int dotIndex = fileName.lastIndexOf(".");
                if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                    fileExtension = fileName.substring(dotIndex + 1).toLowerCase();
                } else {
                    //Use Default if not
                    saveFile = new File(saveFile.getAbsolutePath() + "." + fileExtension);
                }

                ImageIO.write(SwingFXUtils.fromFXImage(image, null), fileExtension, saveFile);
                currentFile = saveFile;
                PaintApplication.isSaved = true;
            }
            catch (IOException ex) {
                System.out.println("Error");
            }
        }
    }
    //Help Pop-Up
    public static void handleHelp(Popup helpPopup, Stage primaryStage) {
        VBox popupRoot = new VBox(15);
        popupRoot.setAlignment(Pos.CENTER);
        popupRoot.setStyle("-fx-padding: 15; " + "-fx-background-color: #D3D3D3;");

        Label titleLabel = new Label("Freeman PaintFX v4.6.0");
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
    //Close Intercept
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
                handleSaveAs(imageSnap, windowSnap);
            }
            else if(result.get() == buttonDiscard) {
                return;
            }
            else {
                event.consume();
            }
        }
    }

    //Get and Set Current File
    public static File getCurrentFile() {return currentFile;}
    public static void setCurrentFile(File file) {currentFile = file;}
}