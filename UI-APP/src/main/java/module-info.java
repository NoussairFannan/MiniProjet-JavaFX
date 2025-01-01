module com.mri.uiapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires opencv;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires json;
    requires java.sql;

    opens com.mri.uiapp to javafx.fxml;
    exports com.mri.uiapp;
}