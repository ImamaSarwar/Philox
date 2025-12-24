package com.example.philoxapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class VolunteerSidePanelController {

    @FXML private ImageView logoImage;
    @FXML private Label initialsLabel;
    @FXML private Label userNameLabel;
    @FXML private ImageView dashboardIcon, opportunitiesIcon, applicationsIcon, profileIcon, settingsIcon;
    @FXML private HBox dashboardBox, opportunitiesBox, applicationsBox, profileBox, settingsBox;
    @FXML private ImageView logoutIcon;
    @FXML
    public void initialize() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/assets/philox-logo.png"));
            logoImage.setImage(logo);
            dashboardIcon.setImage(new Image(getClass().getResourceAsStream("/assets/dashboardIcon.png")));
            opportunitiesIcon.setImage(new Image(getClass().getResourceAsStream("/assets/opportunitiesIcon.png")));
            applicationsIcon.setImage(new Image(getClass().getResourceAsStream("/assets/applicationsIcon.png")));
            profileIcon.setImage(new Image(getClass().getResourceAsStream("/assets/profile-green.png")));
            settingsIcon.setImage(new Image(getClass().getResourceAsStream("/assets/settingsIcon.png")));
            logoutIcon.setImage(new Image(getClass().getResourceAsStream("/assets/logout.png")));
        } catch (Exception e) {
            System.err.println("Logo image not found: " + e.getMessage());
        }



        // Dummy user name
        String fullName = "Sarah Arslan";
        userNameLabel.setText(fullName);
        initialsLabel.setText(generateInitials(fullName));
    }
    private String generateInitials(String name) {
        String[] parts = name.trim().split(" ");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }

    @FXML
    private void handleDashboardClick() {
        showComingSoon("Dashboard");
    }

    @FXML
    private void handleOpportunitiesClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/volunteerOpportunities.fxml"));
            Parent profileRoot = loader.load();
            Scene profileScene = new Scene(profileRoot);
            Stage stage = (Stage) profileBox.getScene().getWindow();
            stage.setScene(profileScene);
            stage.setTitle("Philox - My Profile");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to load profile screen", e.getMessage());
        }
    }

    @FXML
    private void handleApplicationsClick() {
        showComingSoon("My Applications");
    }

    @FXML
    private void handleSettingsClick() {
        showComingSoon("Settings");
    }

    @FXML
    private void handleProfileClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/volunteerProfile.fxml"));
            Parent profileRoot = loader.load();
            Scene profileScene = new Scene(profileRoot);
            Stage stage = (Stage) profileBox.getScene().getWindow();
            stage.setScene(profileScene);
            stage.setTitle("Philox - My Profile");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to load profile screen", e.getMessage());
        }
    }

    @FXML
    private void handleLogoutClick() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("Are you sure you want to log out?");
        confirm.setContentText("Click Yes to return to the landing page.");

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(yes, cancel);

        confirm.showAndWait().ifPresent(response -> {
            if (response == yes) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/landingPage.fxml"));
                    Parent landingRoot = loader.load();
                    Scene landingScene = new Scene(landingRoot);
                    Stage stage = (Stage) initialsLabel.getScene().getWindow();
                    stage.hide();
                    stage.setScene(landingScene);
                    stage.sizeToScene();
                    stage.show();
                    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                    stage.setX(screenBounds.getMinX());
                    stage.setY(screenBounds.getMinY());
                    stage.setWidth(screenBounds.getWidth());
                    stage.setHeight(screenBounds.getHeight());
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Unable to load landing page", e.getMessage());
                }
            }
        });
    }

    private void showComingSoon(String featureName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(null);
        alert.setContentText(featureName + " screen is not implemented yet.");
        alert.showAndWait();
    }

    private void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}