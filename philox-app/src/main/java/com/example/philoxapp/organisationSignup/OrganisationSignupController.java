package com.example.philoxapp.organisationSignup;

import entity.Organisation;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import service.RegistrationService;

public class OrganisationSignupController {

    @FXML private TextField nameField, emailField, locationField, contactField;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private TextArea missionField;
    @FXML private Button loginButton, continueButton, backButton;
    @FXML private ImageView logoImage, backImage;

    @FXML private Label emailErrorLabel, passwordErrorLabel, contactErrorLabel, missionErrorLabel;
    @FXML private Label missionCharCountLabel;

    private Organisation organisation = new Organisation();

    @FXML
    private void initialize() {
        missionField.textProperty().addListener((obs, oldText, newText) -> {
            int charCount = newText.length();
            missionCharCountLabel.setText(charCount + "/500");

            if (charCount > 500) {
                missionField.setText(oldText); // revert to previous valid text
            }

            // Optional: color feedback
            if (charCount < 100 || charCount > 500) {
                missionCharCountLabel.setStyle("-fx-text-fill: #E24560; -fx-font-size: 12px;");
            } else {
                missionCharCountLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
            }
        });
        Image backIconImage = new Image(getClass().getResourceAsStream("/assets/back.png"));
        backImage.setImage(backIconImage);
        Image logo = new Image(getClass().getResourceAsStream("/assets/philox-logo.png"));
        logoImage.setImage(logo);
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
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;

        //prefill fields
        nameField.setText(organisation.getName());
        emailField.setText(organisation.getEmail());
        locationField.setText(organisation.getAddress());
        contactField.setText(organisation.getContactNumber());
        missionField.setText(organisation.getMission());
        passwordField.setText(organisation.getPassword());
        confirmPasswordField.setText(organisation.getPassword());
    }

    // --- Inline Validation Methods ---
    @FXML
    private void validateEmail() {
        boolean valid = emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$");
        emailErrorLabel.setText(valid ? "" : "Invalid email format!");
        emailErrorLabel.setVisible(!valid);
        emailErrorLabel.setManaged(!valid);
    }

    @FXML
    private void validatePassword() {
        boolean valid = passwordField.getText().length() >= 8;
        passwordErrorLabel.setText(valid ? "" : "Password must be at least 8 characters");
        passwordErrorLabel.setVisible(!valid);
        passwordErrorLabel.setManaged(!valid);
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
    private void validateContact() {
        boolean valid = contactField.getText().matches("^03\\d{2}-\\d{7}$");
        contactErrorLabel.setText(valid ? "" : "Invalid contact number format! (e.g., 0300-1234567)");
        contactErrorLabel.setVisible(!valid);
        contactErrorLabel.setManaged(!valid);
    }

    // --- Form Validation ---
    private boolean validateForm() {
        if (isEmpty(nameField) || isEmpty(emailField) || isEmpty(locationField) || isEmpty(contactField)
                || isEmpty(passwordField) || isEmpty(confirmPasswordField) || isEmpty(missionField)) {
            showError("All fields are required.");
            return false;
        }

        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Invalid email format.");
            return false;
        }

        if (!contactField.getText().matches("^03\\d{2}-\\d{7}$")) {
            showError("Invalid contact number format.");
            return false;
        }

        int charCount = missionField.getText().length();
        if (charCount < 100 || charCount > 500) {
            showError("Mission statement must be between 100 and 500 characters.");
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

        if (RegistrationService.userEmailExists(emailField.getText())) {
            showError("This email is already registered. Please use a different email or try logging in.");
            return false;
        }

        return true;
    }

    // --- Helper Methods ---
    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private boolean isEmpty(PasswordField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private boolean isEmpty(TextArea field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // --- Navigation ---
    @FXML
    private void handleContinue() {
        if (!validateForm()) return;

        organisation.setName(nameField.getText().trim());
        organisation.setEmail(emailField.getText().trim());
        organisation.setAddress(locationField.getText().trim());
        organisation.setContactNumber(contactField.getText().trim());
        organisation.setPassword(passwordField.getText().trim());
        organisation.setMission(missionField.getText().trim());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/OrganisationSignupStep2.fxml"));
            Parent step2Root = loader.load();

            OrganisationSignupStep2Controller step2Controller = loader.getController();
            step2Controller.setOrganisation(organisation);

            Scene step2Scene = new Scene(step2Root);
            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setScene(step2Scene);
            stage.setTitle("Philox - Organization Signup (Step 2)");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load Step 2: " + e.getMessage());
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
            showError("Unable to load login screen: " + e.getMessage());
        }
    }
}