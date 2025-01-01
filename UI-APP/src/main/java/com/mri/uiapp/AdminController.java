package com.mri.uiapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class AdminController {

    @FXML
    private BorderPane BorderPaneAdminSubject;

    @FXML
    private Button btnAdminAdd;

    @FXML
    private Button btnAdminMain;


    @FXML
    private void initialize() throws IOException {
        setSubject("/com/mri/uiapp/Admin-Views/Admin-TableView.fxml");
    }

    @FXML
    protected void onSwitchSubjectToMainAdmin(ActionEvent event) throws IOException {
        setSubject("/com/mri/uiapp/Admin-Views/Admin-TableView.fxml");
    }

    @FXML
    protected void onSwitchSubjectToUsers(ActionEvent event) throws IOException {
        setSubject("/com/mri/uiapp/Admin-Views/Admin-TableUsers.fxml");
    }

    @FXML
    protected void onSwitchSubjectToAddAdmin(ActionEvent event) throws IOException {
        setSubject("/com/mri/uiapp/Admin-Views/Admin-AddView.fxml");
    }

    private void setSubject(String view) throws IOException {
        BorderPane borderPane = FXMLLoader.load(getClass().getResource(view));
        BorderPaneAdminSubject.setCenter(borderPane);
    }
}
