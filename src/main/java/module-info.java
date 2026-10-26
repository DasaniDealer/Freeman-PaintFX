module com.paintfx.freemanpaintfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.swing;
    requires jdk.httpserver;
    requires org.kordamp.ikonli.fontawesome5;

    opens com.paintfx.freemanpaintfx to javafx.fxml;
    exports com.paintfx.freemanpaintfx;
}