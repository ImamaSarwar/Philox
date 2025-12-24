package com.example.philoxapp.initialScreens;

import com.example.philoxapp.dashboard.AdminDashboardController;
import com.example.philoxapp.dashboard.OrganisationDashboardController;
import com.example.philoxapp.dashboard.VolunteerDashboardController;
import entity.Admin;
import entity.User;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import service.auth.AuthService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;


public class LoginController {

    @FXML private TextField emailField;
    @FXML private Label emailErrorLabel;
    @FXML private PasswordField passwordField;
    //@FXML private CheckBox rememberMeCheckbox;
    @FXML private Button loginButton;
    @FXML private Button signupButton;
    @FXML private ImageView logoImage;

    @FXML
    public void initialize() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/assets/philox-logo.png"));
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.err.println("Logo image not found: " + e.getMessage());
        }

        // Hover effect: Login button
        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle("-fx-background-color: #3e9641; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 15px; " +
                    "-fx-padding: 11 0; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.3, 0, 3);");
            loginButton.setScaleX(1.03);
            loginButton.setScaleY(1.05);
        });

        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle("-fx-background-color: #4CAF50; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 15px; " +
                    "-fx-padding: 10 0;");
            loginButton.setScaleX(1.0);
            loginButton.setScaleY(1.0);
        });

        // Hover effect: Sign Up button
        signupButton.setOnMouseEntered(e -> {
            signupButton.setStyle("-fx-background-color: #274C56; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: #274C56; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 14px; " +
                    "-fx-padding: 9 25; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.3, 0, 2);");
            signupButton.setScaleX(1.03);
            signupButton.setScaleY(1.05);
        });

        signupButton.setOnMouseExited(e -> {
            signupButton.setStyle("-fx-background-color: transparent; " +
                    "-fx-text-fill: #274C56; " +
                    "-fx-border-color: #274C56; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 14px; " +
                    "-fx-padding: 8 24;");
            signupButton.setScaleX(1.0);
            signupButton.setScaleY(1.0);
        });
    }

    @FXML
    private void handleSignupRedirect() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/landingPage.fxml"));
            Parent landingRoot = loader.load();
            Scene landingScene = new Scene(landingRoot);

            Stage stage = (Stage) signupButton.getScene().getWindow();
            stage.hide();
            stage.setScene(landingScene);
            stage.sizeToScene();
            stage.show();
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setTitle("Philox - Volunteer Signup");
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to load volunteer signup screen");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void validateEmail() {
        boolean valid = emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$");
        emailErrorLabel.setText(valid ? "" : "Invalid email format!");
        emailErrorLabel.setVisible(!valid);
        emailErrorLabel.setManaged(!valid);
    }

    // add validate form logic


    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        User userObj = AuthService.login(email, password);

        if (userObj == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid email or password. Please try again.");
            alert.showAndWait();
            return;
        }

        //print for debugging
        System.out.println("User object returned from AuthController.login: " + userObj);

        // Start user session
        session.Session.getSession().setCurrentUser(userObj);

        if (userObj instanceof entity.Volunteer) {
            entity.Volunteer volunteer = (entity.Volunteer) userObj;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/volunteerDashboard.fxml"));
                Parent dashboardRoot = loader.load();
                VolunteerDashboardController dashboardController = loader.getController();

                // i only need this, rest is editable
                dashboardController.setVolunteer(volunteer);

                Scene dashboardScene = new Scene(dashboardRoot);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(dashboardScene);
                stage.setTitle("Philox - Volunteer Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Unable to load volunteer dashboard");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
            return;
        }
        else if (userObj instanceof entity.Organisation) {
            entity.Organisation organisation = (entity.Organisation) userObj;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/organisationDashboard.fxml"));
                Parent dashboardRoot = loader.load();
                OrganisationDashboardController dashboardController = loader.getController();

                // i only need this, rest is editable
                dashboardController.setOrganisation(organisation);

                Scene dashboardScene = new Scene(dashboardRoot);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(dashboardScene);
                stage.setTitle("Philox - Organisation Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Unable to load organisation dashboard");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
            return;
        }
        else if (userObj instanceof Admin) {
            Admin admin = (Admin) userObj;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/AdminDashboard.fxml"));
                Parent dashboardRoot = loader.load();
                AdminDashboardController dashboardController = loader.getController();

                // i only need this, rest is editable
                dashboardController.setAdmin(admin);

                Scene dashboardScene = new Scene(dashboardRoot);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(dashboardScene);
                stage.setTitle("Philox - Admin Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Unable to load admin dashboard");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login");
        alert.setHeaderText(null);
        alert.setContentText("Invalid user.");
        alert.showAndWait();
    }


}