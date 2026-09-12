package com.paintfx.freemanpaintfx;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SaveFeature {
    private static File currentFile = null;

    public static void save(Image image, Window primaryWindow) {
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
                saveAs(image, primaryWindow);
            }
        }
        //Save As
        else {
            saveAs(image, primaryWindow);
        }
    }

    //Save As Function
    public static void saveAs(Image image, Window primaryWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");

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
            }
            catch (IOException ex) {
                System.out.println("Error");
            }
        }
    }
    //Get and Set Current File
    public static File getCurrentFile() {
        return currentFile;
    }

    public static void setCurrentFile(File file) {
        currentFile = file;
    }
}

