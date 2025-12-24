package com.example.philoxapp.organisationSignup;
import entity.Organisation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class OrganisationSignupStep2Controller {

    // --- FXML Fields ---
    @FXML private ImageView logoImage;
    @FXML private Button loginButton, continueButton;
    @FXML private Button backButton;

    // Social Media
    @FXML private TextField websiteField, linkedInField, instagramField, facebookField;

    // Representative Details
    @FXML private TextField repNameField, repPhoneField, repEmailField, repCnicField;

    // Validation Labels
    @FXML private Label websiteErrorLabel, phoneErrorLabel, repEmailErrorLabel, cnicErrorLabel;

    @FXML private CheckBox termsCheckbox;

    // This object will hold the data from Step 1
    private Organisation organisation; // CORRECT FIELD NAME

    // This method is CALLED BY STEP 1 to pass the data
    public void setOrganisation(Organisation org) {
        this.organisation = org; // Use the correct field name 'organisation'

        // prefill fields if data exists (from the bottom/right side of the conflict)
        websiteField.setText(org.getWebsite() != null ? org.getWebsite() : "");
        linkedInField.setText(org.getLinkedInLink() != null ? org.getLinkedInLink() : "");
        instagramField.setText(org.getInstagramLink() != null ? org.getInstagramLink() : "");
        facebookField.setText(org.getFacebookLink() != null ? org.getFacebookLink() : "");

        // Representative Details
        repNameField.setText(org.getRepName() != null ? org.getRepName() : "");
        repPhoneField.setText(org.getRepContactNumber() != null ? org.getRepContactNumber() : ""); // Corrected getContactNumber to getRepContactNumber (assuming entity has this)
        repCnicField.setText(org.getRepCnic() != null ? org.getRepCnic() : "");
        repEmailField.setText(org.getRepEmail() != null ? org.getRepEmail() : "");
    }

    @FXML
    public void initialize() {
        // Load Logo
        try {
            Image logo = new Image(getClass().getResourceAsStream("/assets/philox-logo.png"));
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.err.println("Logo image not found: " + e.getMessage());
        }

        // Add listeners to enable/disable submit button
        termsCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> updateContinueButtonState());

        // Add live validation listeners
        websiteField.setOnKeyReleased(e -> validateWebsite());
        repPhoneField.setOnKeyReleased(e -> validatePhone());
        repEmailField.setOnKeyReleased(e -> validateRepEmail());
        repCnicField.setOnKeyReleased(e -> validateCNIC());

        // --- Button Hover Effects ---
        loginButton.setOnMouseEntered(e -> setLoginHover(true));
        loginButton.setOnMouseExited(e -> setLoginHover(false));
        continueButton.setOnMouseEntered(e -> setContinueHover(true));
        continueButton.setOnMouseExited(e -> setContinueHover(false));

        backButton.setOnMouseEntered(e -> setBackHover(true));
        backButton.setOnMouseExited(e -> setBackHover(false));
        // Style Checkbox
        styleCheckbox(termsCheckbox);
    }

    // --- Validation ---

    private void updateContinueButtonState() {
        // Only enable if terms are checked
        continueButton.setDisable(!termsCheckbox.isSelected());
    }

    @FXML private void validateWebsite() {
        boolean valid = websiteField.getText().matches("^https?://[\\w\\-.]+\\.[\\w\\-./?%&=]+$");
        websiteErrorLabel.setText(valid ? "" : "Invalid URL (use http:// or https://)");
        websiteErrorLabel.setVisible(!valid); websiteErrorLabel.setManaged(!valid);
    }

    @FXML private void validatePhone() {
        boolean valid = repPhoneField.getText().matches("^03\\d{2}-\\d{7}$");
        phoneErrorLabel.setText(valid ? "" : "Invalid phone (use 03XX-XXXXXXX)");
        phoneErrorLabel.setVisible(!valid); phoneErrorLabel.setManaged(!valid);
    }

    @FXML private void validateRepEmail() {
        boolean valid = repEmailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$");
        repEmailErrorLabel.setText(valid ? "" : "Invalid email format!");
        repEmailErrorLabel.setVisible(!valid); repEmailErrorLabel.setManaged(!valid);
    }

    @FXML private void validateCNIC() {
        boolean valid = repCnicField.getText().matches("^\\d{5}-\\d{7}-\\d{1}$");
        cnicErrorLabel.setText(valid ? "" : "Invalid CNIC (use 12345-1234567-1)");
        cnicErrorLabel.setVisible(!valid); cnicErrorLabel.setManaged(!valid);
    }

    private boolean validateForm() {
        if (isEmpty(websiteField) || isEmpty(repNameField) || isEmpty(repPhoneField) || isEmpty(repEmailField) || isEmpty(repCnicField)) {
            System.out.println("VALIDATION FAILED: One or more required fields are empty.");
            showError("All fields marked with * are required.");
            return false;
        }
        if (!websiteField.getText().matches("^https?://[\\w\\-.]+\\.[\\w\\-./?%&=]+$")) {
            System.out.println("VALIDATION FAILED: Website format is incorrect. Needs http:// or https://");
            showError("Invalid website URL format (must start with http:// or https://).");
            return false;
        }
        if (!repPhoneField.getText().matches("^03\\d{2}-\\d{7}$")) {
            System.out.println("VALIDATION FAILED: Phone format is incorrect. Needs 03XX-XXXXXXX");
            showError("Invalid representative phone number format (use 03XX-XXXXXXX).");
            return false;
        }
        if (!repEmailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            System.out.println("VALIDATION FAILED: Rep Email format is incorrect.");
            showError("Invalid representative email format.");
            return false;
        }
        if (!repCnicField.getText().matches("^\\d{5}-\\d{7}-\\d{1}$")) {
            System.out.println("VALIDATION FAILED: CNIC format is incorrect. Needs dashes.");
            showError("Invalid representative CNIC format (use 12345-1234567-1).");
            return false;
        }
        if (!termsCheckbox.isSelected()) {
            System.out.println("VALIDATION FAILED: Terms checkbox is not checked.");
            showError("You must agree to the terms & community guidelines.");
            return false;
        }

        // If it passes all checks
        System.out.println("Validation PASSED. Proceeding to Step 3.");
        return true;
    }

    // --- Navigation ---

    @FXML
    private void handleSaveAndContinue() {
        // 1. Check if the button click is even registering
        System.out.println("1. 'Save & Continue' (Step 2) button clicked.");

        if (!validateForm()) {
            // validateForm() already has its own print statements,
            // so if it fails, you'll see the reason in the terminal.
            System.out.println("1b. VALIDATION FAILED. Halting navigation.");
            return;
        }

        // 2. If validation passed, we'll see this message
        System.out.println("2. Validation PASSED. Attempting to update data object...");

        try {
            // 3. Check if organisation is null
            if (organisation == null) {
                System.out.println("---!! CRITICAL ERROR AT STEP 3 !!---");
                System.out.println("The 'organisation' object is NULL.");
                System.out.println("This happens if you launch Step 2 directly.");
                System.out.println("Please launch 'OrganisationSignup.fxml' (Step 1) to fix this.");
                showError("Critical Error: Organisation data is missing. Please start from Step 1.");
                return;
            }

            // 4. Try to add Step 2 data to our object
            organisation.setWebsite(websiteField.getText().trim());
            organisation.setRepName(repNameField.getText().trim());
            organisation.setRepCnic(repCnicField.getText().trim());
            organisation.setRepContactNumber(repPhoneField.getText().trim());
            organisation.setRepEmail(repEmailField.getText().trim());

            // Add optional social links
            organisation.setLinkedInLink(linkedInField.getText().trim());
            organisation.setInstagramLink(instagramField.getText().trim());
            organisation.setFacebookLink(facebookField.getText().trim());
            System.out.println("4. Data object updated successfully.");

        } catch (Exception e) {
            System.out.println("---!! ERROR AT STEP 3 or 4 !!---");
            System.out.println("Failed to update organisation object.");
            e.printStackTrace(); // This will print the NullPointerException
            return;
        }

        try {
            // 5. Try to find the FXML file
            System.out.println("5. Locating FXML file: /com/example/philoxapp/OrganisationSignupStep3.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/OrganisationSignupStep3.fxml"));

            if (loader.getLocation() == null) {
                System.out.println("---!! ERROR AT STEP 5 !!---");
                System.out.println("FXML file NOT FOUND. Check the path and file name.");
                showError("Critical Error: Cannot find Step 3 FXML file.");
                return;
            }

            // 6. Try to load the FXML
            System.out.println("6. Loading FXML file...");
            Parent step3Root = loader.load();
            System.out.println("7. FXML loaded successfully.");

            // 8. Try to get the controller
            OrganisationSignupStep3Controller step3Controller = loader.getController();
            System.out.println("8. Controller loaded successfully.");

            // 9. Try to pass data
            step3Controller.setOrganisation(organisation);
            System.out.println("9. Data passed to Step 3.");

            // 10. Try to change the scene
            Scene step3Scene = new Scene(step3Root);
            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setScene(step3Scene);
            stage.setTitle("Philox - Organization Signup (Step 3)");
            System.out.println("10. Navigation to Step 3 COMPLETE.");

        } catch (Exception e) {
            System.out.println("---!! CATASTROPHIC ERROR (Steps 5-10) !!---");
            System.out.println("Failed to load Step 3. The error is:");
            e.printStackTrace(); // This will print the full Java error
            showError("Failed to load Step 3: " + e.getMessage());
        }
    }

    @FXML
    private void handleGoBack() {
        try {
            // Load Step 1
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/OrganisationSignup.fxml"));
            Parent step1Root = loader.load();

            // Pass data *back* to Step 1 to re-fill fields
            OrganisationSignupController step1Controller = loader.getController();
            // We can't re-fill fields on Step 1 as it doesn't have a method for it.
            // But we can just reload it.
            // A better implementation would pass the object back.
            step1Controller.setOrganisation(organisation);

            Scene step1Scene = new Scene(step1Root);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(step1Scene);
            stage.setTitle("Philox - Organization Signup (Step 1)");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load Step 1: " + e.getMessage());
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

    // --- Helpers & Styling ---

    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void setLoginHover(boolean hover) {
        if (hover) {
            loginButton.setStyle("-fx-background-color: #274C56; -fx-text-fill: white; -fx-border-color: #274C56; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 9 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.3, 0, 2);");
            loginButton.setScaleX(1.03); loginButton.setScaleY(1.05);
        } else {
            loginButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #274C56; -fx-border-color: #274C56; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 24;");
            loginButton.setScaleX(1.0); loginButton.setScaleY(1.0);
        }
    }

    private void setContinueHover(boolean hover) {
        if (hover) {
            continueButton.setStyle("-fx-background-color: #3e9641; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 11 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.3, 0, 3);");
            continueButton.setScaleX(1.03); continueButton.setScaleY(1.05);
        } else {
            continueButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 10 0;");
            continueButton.setScaleX(1.0); continueButton.setScaleY(1.0);
        }
    }
    private void setBackHover(boolean hover) {
        if (hover) {
            backButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-border-color: #aaa;" +
                            "-fx-text-fill: #555;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 13px;" +
                            "-fx-padding: 7 14;" +
                            // instead of drop shadow on the entire node:
                            "-fx-border-width: 1.5;" +
                            "-fx-effect: none;" +  // remove global effect
                            "-fx-border-insets: 1;" +
                            "-fx-border-style: solid;"
            );
            backButton.setScaleX(1.05);
            backButton.setScaleY(1.07);
        } else {
            backButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-border-color: #aaa;" +
                            "-fx-text-fill: #555;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 13px;" +
                            "-fx-padding: 6 12;" +
                            "-fx-border-width: 1;"
            );
            backButton.setScaleX(1.0);
            backButton.setScaleY(1.0);
        }
    }
    private void styleCheckbox(CheckBox checkbox) {
        Platform.runLater(() -> {
            checkbox.applyCss(); checkbox.layout();
            Node box = checkbox.lookup(".box");
            if (box != null) {
                box.setStyle(checkbox.isSelected()
                        ? "-fx-background-color: #274C56; -fx-border-color: #274C56; -fx-background-radius: 6; -fx-border-radius: 6;"
                        : "-fx-background-color: transparent; -fx-border-color: #274C56; -fx-background-radius: 6; -fx-border-radius: 6;");
            }
            checkbox.setStyle("-fx-font-size: 13px; -fx-text-fill: #444; " +
                    (checkbox.isSelected() ? "-fx-mark-color: white;" : "-fx-mark-color: #274C56;"));
        });

        checkbox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            checkbox.applyCss(); checkbox.layout();
            Node box = checkbox.lookup(".box");
            if (box != null) {
                box.setStyle(isSelected
                        ? "-fx-background-color: #274C56; -fx-border-color: #274C56; -fx-background-radius: 6; -fx-border-radius: 6;"
                        : "-fx-background-color: transparent; -fx-border-color: #274C56; -fx-background-radius: 6; -fx-border-radius: 6;");
            }
            checkbox.setStyle("-fx-font-size: 13px; -fx-text-fill: #444; " +
                    (isSelected ? "-fx-mark-color: white;" : "-fx-mark-color: #274C56;"));
        });
    }
}