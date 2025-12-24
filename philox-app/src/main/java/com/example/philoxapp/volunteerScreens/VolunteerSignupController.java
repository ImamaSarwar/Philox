package com.example.philoxapp.volunteerScreens;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import service.RegistrationService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VolunteerSignupController {

    @FXML private TextField nameField, emailField, phoneField, cnicField, ageField, cityField;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private CheckBox termsCheckbox;
    @FXML private Button loginButton;
    @FXML private Button createAccountButton, backButton;

    @FXML private Label emailErrorLabel, cnicErrorLabel, phoneErrorLabel, ageErrorLabel, passwordErrorLabel;
    @FXML private ImageView logoImage, backImage;

    @FXML
    public void initialize() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/assets/philox-logo.png"));
            logoImage.setImage(logo);
            Image backIconImage = new Image(getClass().getResourceAsStream("/assets/back.png"));
            backImage.setImage(backIconImage);
        } catch (Exception e) {
            System.err.println("back Icon image not found: " + e.getMessage());
        }

        // Hover effect: enter
        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle("-fx-background-color: #274C56; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: #274C56; " +
                    "-fx-border-radius: 10; " +  // more rounded
                    "-fx-background-radius: 10; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 14px; " +
                    "-fx-padding: 9 25; " +  // slightly taller and wider
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.3, 0, 2);");
            loginButton.setScaleX(1.03);
            loginButton.setScaleY(1.05);
        });

// Hover effect: exit
        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle("-fx-background-color: transparent; " +
                    "-fx-text-fill: #274C56; " +
                    "-fx-border-color: #274C56; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 14px; " +
                    "-fx-padding: 8 24;");
            loginButton.setScaleX(1.0);
            loginButton.setScaleY(1.0);
        });

        // Create Account button hover
        createAccountButton.setOnMouseEntered(e -> {
            createAccountButton.setStyle("-fx-background-color: #3e9641; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 15px; " +
                    "-fx-padding: 11 0; " +  // taller
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.3, 0, 3);");
            createAccountButton.setScaleX(1.03);
            createAccountButton.setScaleY(1.05);
        });

        createAccountButton.setOnMouseExited(e -> {
            createAccountButton.setStyle("-fx-background-color: #4CAF50; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 15px; " +
                    "-fx-padding: 10 0;");
            createAccountButton.setScaleX(1.0);
            createAccountButton.setScaleY(1.0);
        });

        Platform.runLater(() -> {
            termsCheckbox.applyCss();
            termsCheckbox.layout();

            Node box = termsCheckbox.lookup(".box");
            if (box != null) {
                boolean isSelected = termsCheckbox.isSelected();
                box.setStyle(isSelected
                        ? "-fx-background-color: #274C56; " +
                        "-fx-border-color: #274C56; " +
                        "-fx-background-radius: 6; " +
                        "-fx-border-radius: 6;"
                        : "-fx-background-color: transparent; " +
                        "-fx-border-color: #274C56; " +
                        "-fx-background-radius: 6; " +
                        "-fx-border-radius: 6;");
            }

            termsCheckbox.setStyle("-fx-font-size: 13px; -fx-text-fill: #444; " +
                    (termsCheckbox.isSelected() ? "-fx-mark-color: white;" : "-fx-mark-color: #274C56;"));
        });

        termsCheckbox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            termsCheckbox.applyCss();
            termsCheckbox.layout();

            Node box = termsCheckbox.lookup(".box");
            if (box != null) {
                box.setStyle(isSelected
                        ? "-fx-background-color: #274C56; " +
                        "-fx-border-color: #274C56; " +
                        "-fx-background-radius: 6; " +
                        "-fx-border-radius: 6;"
                        : "-fx-background-color: transparent; " +
                        "-fx-border-color: #274C56; " +
                        "-fx-background-radius: 6; " +
                        "-fx-border-radius: 6;");
            }

            termsCheckbox.setStyle("-fx-font-size: 13px; -fx-text-fill: #444; " +
                    (isSelected ? "-fx-mark-color: white;" : "-fx-mark-color: #274C56;"));
        });
    }
    @FXML
    private void handleBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/landingPage.fxml"));
            Parent landingRoot = loader.load();
            Scene landingScene = new Scene(landingRoot);
            Stage stage = (Stage) backButton.getScene().getWindow();
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
            alert.setTitle("Navigation Error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to return to the landing page.");
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

    @FXML
    private void validateCNIC() {
        boolean valid = cnicField.getText().matches("^\\d{5}-\\d{7}-\\d{1}$");
        cnicErrorLabel.setText(valid ? "" : "Invalid CNIC format!");
        cnicErrorLabel.setVisible(!valid);
        cnicErrorLabel.setManaged(!valid);
    }

    @FXML
    private void validatePhone() {
        boolean valid = phoneField.getText().matches("^03\\d{2}-\\d{7}$");
        phoneErrorLabel.setText(valid ? "" : "Invalid phone number format!");
        phoneErrorLabel.setVisible(!valid);
        phoneErrorLabel.setManaged(!valid);
    }

    @FXML
    private void validateAge() {
        try {
            int age = Integer.parseInt(ageField.getText());
            boolean valid = age >= 16 && age <= 100;
            ageErrorLabel.setText(valid ? "" : "Age must be between 16 - 100");
            ageErrorLabel.setVisible(!valid);
            ageErrorLabel.setManaged(!valid);
        } catch (NumberFormatException e) {
            ageErrorLabel.setText("Age must be between 16 - 100");
            ageErrorLabel.setVisible(true);
            ageErrorLabel.setManaged(true);
        }
    }

    @FXML
    private void validatePassword() {
        boolean valid = passwordField.getText().length() >= 8;
        passwordErrorLabel.setText(valid ? "" : "Password must be at least 8 characters");
        passwordErrorLabel.setVisible(!valid);
        passwordErrorLabel.setManaged(!valid);
    }

    @FXML
    private void handleSignup() {
        if (!validateForm()) return;

        // get fields
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        String phone = phoneField.getText().trim();
        String cnic = cnicField.getText().trim();
        int age = Integer.parseInt(ageField.getText().trim());
        String city = cityField.getText().trim();

        boolean registration = RegistrationService.registerVolunteer(name, email, password, phone, cnic, age, city);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if (registration){
            alert.setTitle("Account Created!");
            alert.setHeaderText("Success");
            alert.setContentText("Your account has been created.\nLet's build your volunteer profile!");
            alert.showAndWait();
        }
        else{
            alert.setTitle("Account Creation Failed");
            alert.setHeaderText("Error");
            alert.setContentText("Failed to create account. Please try again");            alert.showAndWait();
        }
    }

    private boolean validateForm() {
        if (isEmpty(nameField) || isEmpty(emailField) || isEmpty(phoneField)
                || isEmpty(cnicField) || isEmpty(ageField) || isEmpty(cityField)
                || isEmpty(passwordField) || isEmpty(confirmPasswordField)) {
            showError("All fields are required.");
            return false;
        }

        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Invalid email format.");
            return false;
        }

        if (!cnicField.getText().matches("^\\d{5}-\\d{7}-\\d{1}$")) {
            showError("Invalid CNIC format.");
            return false;
        }

        if (!phoneField.getText().matches("^03\\d{2}-\\d{7}$")) {
            showError("Invalid phone number format.");
            return false;
        }

        try {
            int age = Integer.parseInt(ageField.getText());
            if (age < 16 || age > 100) {
                showError("Age must be between 16 - 100.");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Age must be a valid number.");
            return false;
        }

        if (passwordField.getText().length() < 8) {
            showError("Password must be at least 8 characters.");
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match.");
            return false;
        }

        if (!termsCheckbox.isSelected()) {
            showError("You must agree to the terms & community guidelines.");
            return false;
        }

        // Check for unique field violations
        String email = emailField.getText().trim();
        String cnic = cnicField.getText().trim();

        if (RegistrationService.userEmailExists(email)) {
            showError("This email is already registered. Please use a different email or try logging in.");
            return false;
        }

        if (RegistrationService.volunteerCnicExists(cnic)) {
            showError("This CNIC is already registered. Please verify your CNIC or contact support.");
            return false;
        }

        return true;
    }

    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private boolean isEmpty(PasswordField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void validatePasswordMatch() {
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        boolean bothFilled = password.length() >= 8 && !confirm.isEmpty();
        boolean match = password.equals(confirm);

        if (bothFilled && !match) {
            passwordErrorLabel.setText("Passwords do not match");
            passwordErrorLabel.setVisible(true);
            passwordErrorLabel.setManaged(true);
        } else {
            passwordErrorLabel.setVisible(false);
            passwordErrorLabel.setManaged(false);
            passwordErrorLabel.setText("");
        }
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
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to load login screen");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}