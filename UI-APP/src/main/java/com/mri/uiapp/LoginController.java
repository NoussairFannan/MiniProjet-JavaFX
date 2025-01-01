package com.mri.uiapp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.sql.Connection;
import com.mri.uiapp.DatabaseConnection;


public class LoginController {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    // FXML Controls
    @FXML
    private Button btnLogin;

    @FXML
    private Circle circleCamera;

    @FXML
    private PasswordField inputLoginPassword;

    @FXML
    private TextField inputLoginUsername;


    // Camera and UI state
    private VideoCapture capture;
    private ScheduledExecutorService cameraDaemon;
    private boolean isCameraActive = false;
    private ScheduledExecutorService sendDaemon;

    private User detectedUser;
    private Boolean detected = false;

    // Initialization
    @FXML
    public void initialize() {
        onStartCamera();
        Platform.runLater(() -> {
            circleCamera.getScene().getWindow().setOnCloseRequest(event -> onClose());
        });
    }

    // FXML Actions
    @FXML
    protected void switchToAdminView() throws IOException {
        String username = inputLoginUsername.getText();
        String password = inputLoginPassword.getText();
        System.out.println("Admin { name: "+username+", password: "+password+" }");
        if (validateAdmin(username, password)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin-Views/Admin-view.fxml"));
            Parent adminRoot = loader.load();

            Stage adminStage = new Stage();
            adminStage.setScene(new Scene(adminRoot));
            adminStage.setTitle("Admin Dashboard");
            adminStage.setWidth(850);
            adminStage.setHeight(650);

            // Close the login window
            Stage loginStage = (Stage) inputLoginUsername.getScene().getWindow();
            loginStage.close();

            adminStage.show();
        } else {
            // Show an error dialog
            //showAlert("Login Failed","Invalid username or password.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password.");
            alert.showAndWait();
        }
    }

    private void switchToUserView() throws IOException {
        showAlert("Login Success","Hello "+detectedUser.firstName+" "+detectedUser.secondName);

    }

    // Camera Operations
    protected void onStartCamera() {
        if (!isCameraActive) {
            capture = new VideoCapture(0);
            if (!capture.isOpened()) {
                showAlert("Camera Error", "Failed to initialize the camera. Please check if the device is connected or already in use.");
                return;
            }

            Runnable frameGrabber = () -> {
                Mat displayFrame = new Mat();
                if (capture.read(displayFrame)) {
                    Core.flip(displayFrame.submat(96,480-96,128,640-128), displayFrame,1);
                    Image imageToShow = matToImage(displayFrame);
                    circleCamera.setFill(new ImagePattern(imageToShow));
                }
            };

            Runnable send = () -> {
                Mat faceFrame = new Mat();
                if (capture.read(faceFrame)) {
                    sendImageToBackEnd(faceFrame);
                }
            };


            cameraDaemon = Executors.newSingleThreadScheduledExecutor();
            cameraDaemon.scheduleAtFixedRate(frameGrabber, 0, 20, TimeUnit.MILLISECONDS);
            isCameraActive = true;

            sendDaemon = Executors.newSingleThreadScheduledExecutor();
            sendDaemon.scheduleAtFixedRate(send, 1, 100, TimeUnit.MILLISECONDS);
        }
    }

    private void closeCamThread() {
        if (cameraDaemon != null) {
            cameraDaemon.shutdown();
            try {
                if (!cameraDaemon.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    cameraDaemon.shutdownNow();
                }
            } catch (InterruptedException e) {
                cameraDaemon.shutdownNow();
            }
        }
    }

    private void closeCaptureThread() {
        if (sendDaemon != null) {
            sendDaemon.shutdown();
            try {
                if (!sendDaemon.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    sendDaemon.shutdownNow();
                }
            } catch (InterruptedException e) {
                sendDaemon.shutdownNow();
            }
        }
    }

    private void onClose() {
        if (isCameraActive) {
            isCameraActive = false;

            closeCaptureThread();

            closeCamThread();

            if (capture != null) {
                capture.release();
                capture = null;
            }
        }
    }


    // Utility Methods
    private Image matToImage(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.show();
            // Create a Timeline to close the dialog after 5 seconds
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(2),
                    event -> alert.close()
            ));
            timeline.setCycleCount(1); // Run only once
            timeline.play();
        });
    }
    //Backend communication
    private void sendImageToBackEnd(Mat frame) {

        if (frame == null || frame.empty()) {
            System.out.println("Frame is empty");
            return;
        }
        try {
            System.out.println("Sending image...");
            MatOfByte buffer = new MatOfByte();
            Imgcodecs.imencode(".jpg", frame, buffer);
            byte[] imageBytes = buffer.toArray();

            URL url = new URL("http://localhost:5000/upload");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/octet-stream");

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(imageBytes);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Image uploaded successfully.");
                detectedUser = jsonToUser(connection.getInputStream());
                if (detectedUser != null) {
                    detected = true;
                    Platform.runLater(() -> {
                        try {
                            switchToUserView();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    System.out.println("Detected user: " + detectedUser);
                }
            } else {
                System.out.println("Failed to upload image. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Upload Error", "Failed to upload the image to the backend: " + e.getMessage());
        }

    }

    private User jsonToUser(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder responseBuilder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }
        reader.close();

        JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
        System.out.println("Message: " + jsonResponse.getString("message"));

        if (jsonResponse.has("user")) {
            JSONObject user = jsonResponse.getJSONObject("user");
            int id = user.getInt("id");
            String firstName = user.getString("firstName");
            String secondName = user.getString("secondName");
//        System.out.printf("User ID: %s%nFirst Name: %s%nSecond Name: %s%n", id, firstName, secondName);
            return new User(id, firstName, secondName);
        } else {
            System.out.println("No user data found.");
        }
        return null;
    }

    private boolean validateAdmin(String username, String password) {
        String query = "SELECT * FROM admin_logins WHERE name = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set parameters for the prepared statement
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println(resultSet);
            // If a record exists, return true
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database error occurred while validating admin credentials.");
        }
        return false;
    }
}
