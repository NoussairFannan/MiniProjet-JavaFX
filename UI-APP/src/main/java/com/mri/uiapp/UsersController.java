package com.mri.uiapp;

import com.mri.uiapp.ApiConnector.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UsersController {

    @FXML
    private TableView<User> TableViewUsers;

    @FXML
    private TableColumn<User, String> tableViewUsers1stName;

    @FXML
    private TableColumn<User, String> tableViewUsers2ndName;

    @FXML
    private TableColumn<User, String> tableViewUsersImg;

    @FXML
    private TableColumn<User, Integer> tableViewUsersId;


    @FXML
    public void initialize() {
        prepTableViewUsers();
        populateTableViewUsers();

    }

    private void prepTableViewUsers() {
        tableViewUsersId.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));
        tableViewUsers1stName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableViewUsers2ndName.setCellValueFactory(new PropertyValueFactory<User, String>("secondName"));
        tableViewUsersImg.setCellValueFactory(new PropertyValueFactory<User, String>("secondName"));
    }

    private void populateTableViewUsers() {
        TableViewUsers.setItems(getDataUsers());
    }

    private ObservableList<User> getDataUsers() {
        ObservableList<User> usersCollectionsFX = FXCollections.observableArrayList();
        List<User> usersList = ApiClient.getUser();
        usersCollectionsFX.addAll(usersList);
        return usersCollectionsFX;
    }

    @FXML
    private void handleDeleteUser() {
        // Get the selected user
        User selectedUser = TableViewUsers.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            // Confirm the deletion (optional, for better UX)
            boolean confirmed = showConfirmationDialog("Are you sure you want to delete this user?");
            if (!confirmed) return;

            String deleteQuery = "DELETE FROM users_encodings WHERE id = ?;";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

                preparedStatement.setInt(1, selectedUser.getId());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    TableViewUsers.getItems().remove(selectedUser);
                    System.out.println("User deleted successfully from the database.");
                } else {
                    System.out.println("Failed to delete the user from the database.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Database error occurred while deleting the user.");
                showErrorDialog("Failed to delete the user. Please try again.");
            }

        } else {
            showErrorDialog("No user selected. Please select a user to delete.");
        }
    }


    // Utility methods for dialogs
    private boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
