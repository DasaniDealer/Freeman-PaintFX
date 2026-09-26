package com.paintfx.freemanpaintfx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;

public class DrawSettings extends VBox {

    private final ColorPicker colorPicker;
    private final Slider sizeSlider;
    private final Robot colorGrab;
    private final ToggleButton grabberButton;

    public DrawSettings() {
        this.setSpacing(10);
        this.setStyle("-fx-padding: 5; -fx-background-color: #c5c7ca;");

        //Colours
        Label colorLabel = new Label("Colour");
        colorLabel.setStyle("-fx-font-weight: bold;");

        colorPicker = new ColorPicker();
        colorPicker.setValue(Color.web("#000000"));

        //Colour Grabber
        colorGrab = new Robot();
        grabberButton = new ToggleButton("Colour Grabber");

        //Brush Size
        Label sizeLabel = new Label("Brush Size");
        sizeLabel.setStyle("-fx-font-weight: bold;");

        sizeSlider = new Slider(0, 50, 5);
        sizeSlider.setShowTickLabels(true);

        this.getChildren().addAll(
                colorLabel, colorPicker,
                grabberButton,
                sizeLabel, sizeSlider
        );
    }

    public ColorPicker getColorPicker() {return colorPicker;}
    public Color getColor() { return colorPicker.getValue();}
    public double getSize() { return sizeSlider.getValue();}
    public Robot getColorGrab() {return colorGrab;}
    public ToggleButton getGrabButton() {return grabberButton;}
}