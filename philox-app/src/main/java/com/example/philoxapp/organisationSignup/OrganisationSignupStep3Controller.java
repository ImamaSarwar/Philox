package com.example.philoxapp.organisationSignup;
import entity.Organisation;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.layout.HBox;
import service.RegistrationService;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class OrganisationSignupStep3Controller {

    // --- FXML Fields ---
    @FXML private ImageView logoImage;
    @FXML private Button loginButton, submitButton;
    @FXML private Button backButton;

    // Registration Details
    @FXML private ComboBox<String> registrationAuthorityField;
    @FXML private TextField registrationNumberField;
    @FXML private DatePicker registrationDateField;
    @FXML private TextField ntnField;
    @FXML private Label registrationNumberErrorLabel;
    @FXML private Label ntnErrorLabel;

    // Upload Buttons
    @FXML private Button uploadRegButton, uploadTaxButton, uploadCnicButton;

    // Status Labels for Uploads
    @FXML private Label regStatusLabel, taxStatusLabel, cnicStatusLabel;
    @FXML private HBox regFileBox, taxFileBox, cnicFileBox;
    @FXML private Label regFileNameLabel, taxFileNameLabel, cnicFileNameLabel;

    // Confirmation
    @FXML private CheckBox confirmationCheckbox;

    // --- State Variables ---
    private Organisation organisation; // To hold data from Steps 1 & 2
    private File registrationFile, taxFile, cnicFile; // To hold the files

    private boolean regUploaded = false;
    private boolean taxUploaded = false;
    private boolean cnicUploaded = false;

    // This method is CALLED BY STEP 2
    public void setOrganisation(Organisation org) {
        this.organisation = org;

        // Pre-fill data if it was entered before
        if (org.getNtn() != null) ntnField.setText(org.getNtn());
        if (org.getRegistrationAuthority() != null) registrationAuthorityField.setValue(org.getRegistrationAuthority());
        if (org.getRegistrationNumber() != null) registrationNumberField.setText(org.getRegistrationNumber());

        // Ensure getIssueDate returns a LocalDate for DatePicker
        LocalDate issueDate = org.getIssueDate();
        if (issueDate != null) registrationDateField.setValue(issueDate);
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

        // Submit button always enabled
        submitButton.setDisable(false);

        registrationNumberField.textProperty().addListener((obs, oldVal, newVal) -> validateRegistrationNumber());
        ntnField.textProperty().addListener((obs, oldVal, newVal) -> validateNTN());

        // Populate Registration Authority ComboBox
        registrationAuthorityField.setItems(FXCollections.observableArrayList(
                "Securities and Exchange Commission of Pakistan (SECP)",
                "Directorate of Social Welfare",
                "Pakistan Centre for Philanthropy (PCP)",
                "Other"
        ));

        // Hover effects
        loginButton.setOnMouseEntered(e -> setLoginHover(true));
        loginButton.setOnMouseExited(e -> setLoginHover(false));
        submitButton.setOnMouseEntered(e -> setSubmitHover(true));
        submitButton.setOnMouseExited(e -> setSubmitHover(false));
        styleCheckbox(confirmationCheckbox);
        backButton.setOnMouseEntered(e -> setBackHover(true));
        backButton.setOnMouseExited(e -> setBackHover(false));
    }

    // --- File Upload Handlers ---

    private void updateSubmitButtonState() {
        // This method was referenced in the conflict, but the original code didn't define it.
        // Assuming it's meant to check form validity if needed, but since submitButton is always enabled,
        // we'll leave it as a placeholder or remove the calls if not required.
        // For now, removing the calls from the handlers.
    }


    @FXML
    private void handleUploadRegistration() {
        this.registrationFile = showFileChooser("Upload Registration Proof");
        if (this.registrationFile != null) {
            regFileNameLabel.setText(this.registrationFile.getName());
            regFileBox.setVisible(true);
            regFileBox.setManaged(true);
            regUploaded = true;
            // Preserving logic from the bottom/right version
            organisation.setRegistrationProofPath(registrationFile.getPath());
            // updateSubmitButtonState(); // Removed, as button is always enabled
        }
    }

    @FXML
    private void handleUploadTax() {
        this.taxFile = showFileChooser("Upload Tax Document");
        if (this.taxFile != null) {
            taxFileNameLabel.setText(this.taxFile.getName());
            taxFileBox.setVisible(true);
            taxFileBox.setManaged(true);
            taxUploaded = true;
            // Preserving logic from the bottom/right version
            organisation.setTaxDocumentPath(taxFile.getPath());
            // updateSubmitButtonState(); // Removed, as button is always enabled
        }
    }

    @FXML
    private void handleUploadCnic() {
        this.cnicFile = showFileChooser("Upload Representative's CNIC");
        if (this.cnicFile != null) {
            cnicFileNameLabel.setText(this.cnicFile.getName());
            cnicFileBox.setVisible(true);
            cnicFileBox.setManaged(true);
            cnicUploaded = true;
            // Preserving logic from the bottom/right version
            organisation.setCnicProofPath(cnicFile.getPath());
            // updateSubmitButtonState(); // Removed, as button is always enabled
        }
    }

    private File showFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        Stage stage = (Stage) submitButton.getScene().getWindow();
        return fileChooser.showOpenDialog(stage);
    }

    // --- Submission & Validation ---

    @FXML
    private void handleSubmit() {
        // Validate all fields on submission
        if (!validateStep3Form()) {
            return;
        }

        // 1. Add final data to our Organisation object (Using 'organisation' and correct types)
        organisation.setNtn(ntnField.getText().trim());
        organisation.setRegistrationAuthority(registrationAuthorityField.getValue());
        organisation.setRegistrationNumber(registrationNumberField.getText().trim());

        // Ensure DatePicker value is a LocalDate
        organisation.setIssueDate(registrationDateField.getValue());

        // File paths should already be set in the upload handlers.
        // print file paths for debug
        System.out.println("Reg Proof Path: " + organisation.getRegistrationProofPath());
        System.out.println("Tax Doc Path: " + organisation.getTaxDocumentPath());
        System.out.println("CNIC Proof Path: " + organisation.getCnicProofPath());


        // 2. Call the REAL backend registration logic (Preserving logic from the bottom/right version)
        boolean registrationSuccess = RegistrationService.registerOrganisation(organisation);

        // 3. Show success alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (registrationSuccess){
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("Your organization has been registered successfully!");
            alert.showAndWait();
            handleLoginRedirect();
        } else {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Registration Failed");
            alert.setHeaderText(null);
            alert.setContentText("There was an error during registration. Please try again later.");
            alert.showAndWait();
            //?
        }
    }

    private boolean validateStep3Form() {
        if (registrationAuthorityField.getValue() == null || registrationAuthorityField.getValue().isEmpty()) {
            showError("Please select a **Registration Authority**.");
            return false;
        }

        String regNumber = registrationNumberField.getText().trim();
        if (regNumber.isEmpty()) {
            showError("Please enter your **Registration Number**.");
            return false;
        }
        if (!regNumber.matches("^\\d{7}$")) {
            showError("Invalid **Registration Number** (must be 7 digits, e.g., 0011946).");
            return false;
        }

        if (registrationDateField.getValue() == null) {
            showError("Please select your **Date of Registration**.");
            return false;
        }

        String ntn = ntnField.getText().trim();
        if (ntn.isEmpty()) {
            showError("Please enter your **National Tax Number (NTN)**.");
            return false;
        }
        if (!ntn.matches("^\\d{7}-\\d{3}$")) {
            showError("Invalid **NTN** (format: 1234567-001).");
            return false;
        }

        if (!regUploaded || !taxUploaded || !cnicUploaded) {
            showError("Please upload all three required **documents**.");
            return false;
        }

        if (!confirmationCheckbox.isSelected()) {
            showError("Please **confirm** all information is accurate.");
            return false;
        }

        // Preserving logic from the bottom/right version (RegistrationService checks)
        if (RegistrationService.organisationRegistrationNumberExists(registrationNumberField.getText().trim())) {
            showError("This **Registration Number** is already registered. Please check your details or contact support.");
            return false;
        }
        if (RegistrationService.organisationNtnExists(ntnField.getText().trim())) {
            showError("This **National Tax Number (NTN)** is already registered. Please check your details or contact support.");
            return false;
        }

        return true;
    }

    @FXML
    private void validateNTN() {
        String value = ntnField.getText();
        boolean valid = value.matches("^\\d{7}-\\d{3}$");
        ntnErrorLabel.setText(valid ? "" : "Invalid NTN (format: 1234567-001)");
        ntnErrorLabel.setVisible(!valid);
        ntnErrorLabel.setManaged(!valid);
    }

    @FXML
    private void validateRegistrationNumber() {
        String value = registrationNumberField.getText();
        boolean valid = value.matches("^\\d{7}$");
        registrationNumberErrorLabel.setText(valid ? "" : "Invalid Registration Number (format: 7 digits, e.g., 0011946)");
        registrationNumberErrorLabel.setVisible(!valid);
        registrationNumberErrorLabel.setManaged(!valid);
    }

    // --- Navigation ---

    @FXML
    private void handleGoBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/OrganisationSignupStep2.fxml"));
            Parent step2Root = loader.load();
            OrganisationSignupStep2Controller step2Controller = loader.getController();

            // Resolving the conflict by using the correct method and object name
            step2Controller.setOrganisation(this.organisation); // Pass the object back

            Scene step2Scene = new Scene(step2Root);
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(step2Scene);
            stage.setTitle("Philox - Organization Signup (Step 2)");
        } catch (IOException e) {
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
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Philox - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to load login screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoveRegFile() {
        registrationFile = null;
        regUploaded = false;
        regFileBox.setVisible(false);
        regFileBox.setManaged(false);
    }

    @FXML
    private void handleRemoveTaxFile() {
        taxFile = null;
        taxUploaded = false;
        taxFileBox.setVisible(false);
        taxFileBox.setManaged(false);
    }

    @FXML
    private void handleRemoveCnicFile() {
        cnicFile = null;
        cnicUploaded = false;
        cnicFileBox.setVisible(false);
        cnicFileBox.setManaged(false);
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
                            "-fx-border-width: 1.5;" +
                            "-fx-effect: none;" +
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

    private void setSubmitHover(boolean hover) {
        if (hover) {
            submitButton.setStyle("-fx-background-color: #3e9641; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 11 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.3, 0, 3);");
            submitButton.setScaleX(1.03); submitButton.setScaleY(1.05);
        } else {
            submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 10 0;");
            submitButton.setScaleX(1.0); submitButton.setScaleY(1.0);
        }
    }

    private void styleCheckbox(CheckBox checkbox) {
        Platform.runLater(() -> {
            checkbox.applyCss();
            checkbox.layout();
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
            checkbox.applyCss();
            checkbox.layout();
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