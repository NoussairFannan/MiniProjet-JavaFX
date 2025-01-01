package com.mri.uiapp;

import com.mri.uiapp.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class UserAddController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private Button chooseImageButton;

    @FXML
    private Button saveButton;

    private File selectedImage;

    @FXML
    private void initialize() {
        // Set up the "Choose Image" button action
        chooseImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            selectedImage = fileChooser.showOpenDialog(null);

            if (selectedImage != null) {
                System.out.println("Image selected: " + selectedImage.getAbsolutePath());
            } else {
                System.out.println("No image selected.");
            }
        });

        // Set up the "Save" button action
        saveButton.setOnAction(event -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();

            if (firstName.isEmpty() || lastName.isEmpty() || selectedImage == null) {
                System.out.println("Please fill in all fields and select an image.");
            } else {
                saveUserToDatabase(firstName, lastName, selectedImage);
            }
        });
    }

    private void saveUserToDatabase(String firstName, String lastName, File imageFile) {
        String insertQuery = "INSERT INTO users_encodings ('1st_name', '2nd_name', image_path) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            // Set parameters for the prepared statement
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, imageFile.getAbsolutePath());

            // Execute the insert query
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("User saved successfully to the database.");
                showSuccessDialog("User added successfully to the database.");
            } else {
                System.out.println("Failed to save user to the database.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database error occurred while saving the user.");
        }
    }
    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
