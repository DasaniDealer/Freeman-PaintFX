package com.paintfx.freemanpaintfx;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;


public class SideMenuFeaturesTest {

    //Starts Environment
    @BeforeAll
    public static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @Test
    void grabColor() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        //Application Thread Run
        Platform.runLater(() -> {
            try {
                Group canvas = new Group();
                ToggleGroup toolToggle = new ToggleGroup();

                //DrawSettings
                DrawSettings drawSettings = new DrawSettings();
                ColorPicker colorPicker = new ColorPicker(Color.BLUE); // Safe here!
                drawSettings.setColorPicker(colorPicker);

                System.out.println("--- START COLOR GRAB TEST ---");
                System.out.println("Initial Color Picker value: " + colorPicker.getValue());

                SideMenuFeatures.grabColor(canvas, drawSettings, toolToggle);

                // Construct Fake Click
                MouseEvent fakeClick = new MouseEvent(
                        MouseEvent.MOUSE_CLICKED, 0, 0, 150, 250, MouseButton.PRIMARY, 1,
                        false, false, false, false, true, false, false, true, false, true, null
                );

                canvas.getOnMouseClicked().handle(fakeClick);

                //Print Results
                System.out.println("Updated Color Picker value: " + colorPicker.getValue());
                System.out.println("Canvas Cursor state (Should be DEFAULT): " + canvas.getCursor());
                System.out.println("Active Toggle State (Should be null): " + toolToggle.getSelectedToggle());
                System.out.println("TEST END");

            } finally {
                latch.countDown();
            }
        });

        latch.await();
    }
}