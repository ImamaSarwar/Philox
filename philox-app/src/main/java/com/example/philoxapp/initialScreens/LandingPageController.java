package com.example.philoxapp.initialScreens;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.SnapshotParameters;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import javafx.application.Platform;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class LandingPageController {

    @FXML private Button loginButton;
    @FXML private Button joinVolunteerButton;
    @FXML private Button joinOrgButton;
    @FXML private ImageView logoImage;
    @FXML private ImageView volunteerImage;

    @FXML
    public void initialize() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/assets/philox-logo.png"));
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.err.println("Logo image not found: " + e.getMessage());
        }

        try {
            Image image = new Image(getClass().getResourceAsStream("/assets/volunteerImage.jpg"));
            volunteerImage.setImage(image);

            // Ensure image is scaled smoothly
            volunteerImage.setPreserveRatio(true);
            volunteerImage.setSmooth(true);

            // Apply rounded corners after layout is complete
            Platform.runLater(() -> {
                Bounds bounds = volunteerImage.getLayoutBounds();
                double width = bounds.getWidth();
                double height = bounds.getHeight();

                if (width > 0 && height > 0) {
                    Rectangle clip = new Rectangle(width, height);
                    clip.setArcWidth(40); // Adjust for roundness
                    clip.setArcHeight(40);
                    volunteerImage.setClip(clip);

                    // Optional: snapshot to preserve drop shadow outside clip
                    SnapshotParameters params = new SnapshotParameters();
                    params.setFill(Color.TRANSPARENT);
                    WritableImage roundedImage = volunteerImage.snapshot(params, null);
                    volunteerImage.setClip(null);
                    volunteerImage.setImage(roundedImage);
                }
            });
        } catch (Exception e) {
            System.err.println("Volunteer image not found: " + e.getMessage());
        }

        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle("-fx-background-color: #274C56; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: #274C56; " +
                    "-fx-border-radius: 6; " +
                    "-fx-background-radius: 6; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 14px; " +
                    "-fx-padding: 8 24; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.3, 0, 2);");
            loginButton.setScaleX(1.03);
            loginButton.setScaleY(1.05);
        });

        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle("-fx-background-color: transparent; " +
                    "-fx-text-fill: #274C56; " +
                    "-fx-border-color: #274C56; " +
                    "-fx-border-radius: 6; " +
                    "-fx-background-radius: 6; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 14px; " +
                    "-fx-padding: 8 24;");
            loginButton.setScaleX(1.0);
            loginButton.setScaleY(1.0);
        });

        // Hover effects
        //setupHoverEffect(loginButton, "#274C56", "#fff", "#274C56", "#fff", "#274C56");
        setupHoverEffect(joinVolunteerButton, "#3e9641", "#fff", "#4CAF50", "white", "transparent");
        setupHoverEffect(joinOrgButton, "#274C56", "white", "transparent", "#274C56", "#274C56");
    }

    private void setupHoverEffect(Button button, String hoverBg, String hoverText, String defaultBg, String defaultText, String defaultBorder) {
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + hoverBg + ";" +
                    "-fx-text-fill: " + hoverText + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 10 20;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.3, 0, 2);");
            button.setScaleX(1.03);
            button.setScaleY(1.05);
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + defaultBg + ";" +
                    "-fx-text-fill: " + defaultText + ";" +
                    "-fx-border-color: " + defaultBorder + ";" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 10 20;");
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
    }

    @FXML
    private void handleLoginRedirect() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/login.fxml"));
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Philox - Login");
        } catch (Exception e) {
            showError("Unable to load login screen", e);
        }
    }

    @FXML
    private void handleVolunteerSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/volunteerSignup.fxml"));
            Parent signupRoot = loader.load();
            Scene signupScene = new Scene(signupRoot);

            Stage stage = (Stage) joinVolunteerButton.getScene().getWindow();
            stage.setScene(signupScene);
            stage.setTitle("Philox - Volunteer Signup");
        } catch (Exception e) {
            showError("Unable to load volunteer signup screen", e);
        }
    }

    @FXML
    private void handleOrgSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/organisationSignup.fxml"));
            Parent orgRoot = loader.load();
            Scene orgScene = new Scene(orgRoot);

            Stage stage = (Stage) joinOrgButton.getScene().getWindow();
            stage.setScene(orgScene);
            stage.setTitle("Philox - Organisation Signup");
        } catch (Exception e) {
            showError("Unable to load organisation signup screen", e);
        }
    }

    private void showError(String header, Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}