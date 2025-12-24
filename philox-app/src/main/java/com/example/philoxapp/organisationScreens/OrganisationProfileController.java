
package com.example.philoxapp.organisationScreens;

import com.example.philoxapp.UserProfileController;
import com.example.philoxapp.component.card.RatingCard;
import com.example.philoxapp.component.sidepanel.SidePanel;
import entity.Organisation;
import entity.Rating;
import entity.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.ProfileService;
import service.RatingService;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class OrganisationProfileController extends UserProfileController {

    public enum ViewMode {
        ORGANISATION_SELF_VIEW,
        VOLUNTEER_VIEW
    }

    @FXML private VBox sidebarContainer;

    @FXML private Label mainTitleLabel, mainSubtitleLabel;
    @FXML private Button backButton;
    @FXML private Button editProfileButton;
    @FXML private VBox documentsContainer;

    // Fields
    @FXML private TextField orgNameField;
    @FXML private TextField missionArea;
    @FXML private TextField emailField, phoneField, addressField, websiteField;
    @FXML private TextField repNameField, repCnicField, repEmailField, repContactField;

    // Ratings Container
    @FXML private Label averageRatingLabel;
    @FXML private FlowPane ratingsContainer;

    // Error Labels
    @FXML private Label emailError, phoneError, websiteError, cnicError, repEmailError, repContactError;

    // Document View Buttons
    @FXML private Button viewRegBtn, viewTaxBtn, viewCnicBtn;

    private Organisation organisation;
    private boolean isEditMode = false;
    private ViewMode currentViewMode = ViewMode.ORGANISATION_SELF_VIEW;

    // REGEX
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+92\\d{10}|03\\d{2}-\\d{7})$");
    private static final Pattern CNIC_PATTERN = Pattern.compile("^\\d{5}-\\d{7}-\\d{1}$");
    private static final Pattern WEBSITE_PATTERN = Pattern.compile("^https?://[\\w\\-.]+\\.[\\w\\-./?%&=]+$");


    public void setOrganisation(Organisation org) {
        setOrganisation(org, org, ViewMode.ORGANISATION_SELF_VIEW);
    }

    public void setOrganisation(Organisation org, User viewer, ViewMode mode) {
        this.organisation = org;
        this.currentViewMode = mode;
        setUser(viewer);

        // Set up sidebar only for organisation self-view
        if (mode == ViewMode.ORGANISATION_SELF_VIEW) {
            SidePanel sidePanel = new SidePanel(viewer, "Profile");
            if (sidebarContainer != null) {
                sidebarContainer.getChildren().setAll(sidePanel.getNode());
            }
        } else {
            // Hide sidebar for volunteer view
            if (sidebarContainer != null) {
                sidebarContainer.setVisible(false);
                sidebarContainer.setManaged(false);
            }
        }

        if (mode == ViewMode.VOLUNTEER_VIEW) {
            if (backButton != null) { backButton.setVisible(true); backButton.setManaged(true); }
            if (editProfileButton != null) { editProfileButton.setVisible(false); editProfileButton.setManaged(false); }
            if (documentsContainer != null) { documentsContainer.setVisible(false); documentsContainer.setManaged(false); }

            // Update text content for volunteer view
            updateTextForVolunteerView();
        } else {
            if (backButton != null) { backButton.setVisible(false); backButton.setManaged(false); }
            if (editProfileButton != null) { editProfileButton.setVisible(true); editProfileButton.setManaged(true); }
            if (documentsContainer != null) { documentsContainer.setVisible(true); documentsContainer.setManaged(true); }
        }

        populateFields();
        setupValidationListeners();
        setupDocumentButtons();
        loadRatings();
    }

    private void loadRatings(){
        if (ratingsContainer == null || averageRatingLabel == null) return;

        // Clear existing ratings
        ratingsContainer.getChildren().clear();

        // Get real average rating from service
        double avgRating = RatingService.getAverageRating(organisation.getOrganisationId());
        averageRatingLabel.setText(String.format("%.1f", avgRating));

        // Load last 5 ratings
        List<Rating> ratings = RatingService.getLastNRatings(organisation.getOrganisationId(), 5);

        if (ratings.isEmpty()) {
            Label noRatingsLabel = new Label("No reviews yet");
            noRatingsLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 14px; -fx-padding: 20;");
            ratingsContainer.getChildren().add(noRatingsLabel);
        } else {
            for (Rating r : ratings) {
                RatingCard card = new RatingCard(r);
                ratingsContainer.getChildren().add(card);
            }
        }
    }

    private void populateFields() {
        if (organisation == null) return;
        if (orgNameField != null) orgNameField.setText(organisation.getName());
        if (missionArea != null) missionArea.setText(organisation.getMission());
        if (emailField != null) emailField.setText(organisation.getEmail());
        if (phoneField != null) phoneField.setText(organisation.getContactNumber());
        if (addressField != null) addressField.setText(organisation.getAddress());
        if (websiteField != null) websiteField.setText(organisation.getWebsite());
        if (repNameField != null) repNameField.setText(organisation.getRepName());
        if (repCnicField != null) repCnicField.setText(organisation.getRepCnic());
        if (repEmailField != null) repEmailField.setText(organisation.getRepEmail());
        if (repContactField != null) repContactField.setText(organisation.getRepContactNumber());


    }

    private void setupValidationListeners() {
        if (emailField != null) {
            emailField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (isEditMode) validateField(emailField, emailError, EMAIL_PATTERN, "Invalid format: name@example.com");
            });
        }
        if (phoneField != null) {
            phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (isEditMode) validateField(phoneField, phoneError, PHONE_PATTERN, "Format: +923... or 03XX-XXXXXXX");
            });
        }
        if (websiteField != null) {
            websiteField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (isEditMode) validateField(websiteField, websiteError, WEBSITE_PATTERN, "Must start with http:// or https://");
            });
        }
        if (repCnicField != null) {
            repCnicField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (isEditMode) validateField(repCnicField, cnicError, CNIC_PATTERN, "Format: 12345-1234567-1");
            });
        }
        if (repEmailField != null) {
            repEmailField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (isEditMode) validateField(repEmailField, repEmailError, EMAIL_PATTERN, "Invalid format: name@example.com");
            });
        }
        if (repContactField != null) {
            repContactField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (isEditMode) validateField(repContactField, repContactError, PHONE_PATTERN, "Format: +923... or 03XX-XXXXXXX");
            });
        }
    }

    private boolean validateField(TextField field, Label errorLabel, Pattern pattern, String errorMsg) {
        if (field == null || errorLabel == null) return true;

        String text = field.getText().trim();
        boolean isValid = pattern.matcher(text).matches();

        if (isValid) {
            field.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #ccc; -fx-border-radius: 4; -fx-padding: 8; -fx-text-fill: #333; -fx-font-size: 14px;");
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            errorLabel.setText("");
        } else {
            field.setStyle("-fx-background-color: #FFF5F5; -fx-border-color: #E24560; -fx-border-radius: 4; -fx-padding: 8; -fx-text-fill: #333; -fx-font-size: 14px;");
            errorLabel.setText(errorMsg);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
        return isValid;
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleEditProfile() {
        if (currentViewMode == ViewMode.VOLUNTEER_VIEW) return;

        if (!isEditMode) {
            isEditMode = true;
            editProfileButton.setText("Save Profile");
            editProfileButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");

            toggleFields(true);


        } else {
            boolean v1 = validateField(emailField, emailError, EMAIL_PATTERN, "Invalid format: name@example.com");
            boolean v2 = validateField(phoneField, phoneError, PHONE_PATTERN, "Format: +923... or 03XX-XXXXXXX");
            boolean v3 = validateField(websiteField, websiteError, WEBSITE_PATTERN, "Must start with http:// or https://");
            boolean v4 = validateField(repCnicField, cnicError, CNIC_PATTERN, "Format: 12345-1234567-1");
            boolean v5 = validateField(repEmailField, repEmailError, EMAIL_PATTERN, "Invalid format: name@example.com");
            boolean v6 = validateField(repContactField, repContactError, PHONE_PATTERN, "Format: +923... or 03XX-XXXXXXX");

            if (!v1 || !v2 || !v3 || !v4 || !v5 || !v6) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Please correct the highlighted fields.");
                alert.showAndWait();
                return;
            }

            updateOrganisationModel();
            boolean success = ProfileService.updateOrganisationProfile(organisation);

            if(success) {
                isEditMode = false;
                editProfileButton.setText("Edit Profile");
                editProfileButton.setStyle("-fx-background-color: #009689; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");

                toggleFields(false);

                hideAllErrors();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Profile updated successfully.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Update Failed");
                alert.setHeaderText(null);
                alert.setContentText("Could not update profile. Please check database connection.");
                alert.showAndWait();
            }
        }
    }

    private void hideAllErrors() {
        if(emailError != null) { emailError.setVisible(false); emailError.setManaged(false); }
        if(phoneError != null) { phoneError.setVisible(false); phoneError.setManaged(false); }
        if(websiteError != null) { websiteError.setVisible(false); websiteError.setManaged(false); }
        if(cnicError != null) { cnicError.setVisible(false); cnicError.setManaged(false); }
        if(repEmailError != null) { repEmailError.setVisible(false); repEmailError.setManaged(false); }
        if(repContactError != null) { repContactError.setVisible(false); repContactError.setManaged(false); }
    }

    private void updateOrganisationModel() {
        if (organisation == null) return;
        organisation.setName(orgNameField.getText());
        organisation.setMission(missionArea.getText());
        organisation.setContactNumber(phoneField.getText());
        organisation.setAddress(addressField.getText());
        organisation.setWebsite(websiteField.getText());
        organisation.setRepName(repNameField.getText());
        organisation.setRepCnic(repCnicField.getText());
        organisation.setRepEmail(repEmailField.getText());
        organisation.setRepContactNumber(repContactField.getText());
    }

    private void toggleFields(boolean editable) {
        setHeaderEditable(orgNameField, false);
        setEditable(missionArea, editable);
        setEditable(emailField, false);
        setEditable(phoneField, editable);
        setEditable(addressField, editable);
        setEditable(websiteField, editable);
        setEditable(repNameField, editable);
        setEditable(repCnicField, editable);
        setEditable(repEmailField, editable);
        setEditable(repContactField, editable);

        // Update document button visibility
        setupDocumentButtons();
    }

    private void setHeaderEditable(TextField control, boolean editable) {
        if (control == null) return;
        control.setEditable(editable);
        if (editable) {
            control.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #ccc; -fx-border-radius: 4; -fx-padding: 0 8; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #274C56;");
        } else {
            control.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #274C56;");
        }
    }

    private void setEditable(TextInputControl control, boolean editable) {
        if (control == null) return;
        control.setEditable(editable);
        if (editable) {
            control.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #ccc; -fx-border-radius: 4; -fx-padding: 8; -fx-text-fill: #333; -fx-font-size: 14px;");
        } else {
            control.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-text-fill: #333; -fx-font-size: 14px;");
        }
    }

    private void setupDocumentButtons() {
        if (currentViewMode == ViewMode.VOLUNTEER_VIEW) return;

        // Always show view buttons in non-edit mode for self view
        if (viewRegBtn != null) {
            viewRegBtn.setVisible(!isEditMode);
            viewRegBtn.setManaged(!isEditMode);
        }
        if (viewTaxBtn != null) {
            viewTaxBtn.setVisible(!isEditMode);
            viewTaxBtn.setManaged(!isEditMode);
        }
        if (viewCnicBtn != null) {
            viewCnicBtn.setVisible(!isEditMode);
            viewCnicBtn.setManaged(!isEditMode);
        }
    }

    @FXML
    private void handleViewReg() {
        if (organisation != null && organisation.getRegistrationProofPath() != null) {
            viewDocument(organisation.getRegistrationProofPath(), "Registration Document");
        } else {
            showInfo("No Document", "No registration document is available to view.");
        }
    }

    @FXML
    private void handleViewTax() {
        if (organisation != null && organisation.getTaxDocumentPath() != null) {
            viewDocument(organisation.getTaxDocumentPath(), "Tax Document");
        } else {
            showInfo("No Document", "No tax document is available to view.");
        }
    }

    @FXML
    private void handleViewCnic() {
        if (organisation != null && organisation.getCnicProofPath() != null) {
            viewDocument(organisation.getCnicProofPath(), "CNIC Document");
        } else {
            showInfo("No Document", "No CNIC document is available to view.");
        }
    }

    private void viewDocument(String filePath, String documentType) {
        try {
            File file = new File(filePath);
            if (!file.isAbsolute()) {
                // Prepend working directory if path is relative
                file = new File(System.getProperty("user.dir"), filePath);
            }
            if (file.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                } else {
                    showError("System Error", "Cannot open documents on this system.");
                }
            } else {
                showError("File Not Found", documentType + " file not found at: " + filePath);
            }
        } catch (IOException e) {
            showError("Error Opening Document", "Could not open " + documentType + ": " + e.getMessage());
        }
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updateTextForVolunteerView() {
        // Update the main header text to be appropriate for volunteer viewing organization
        if (mainTitleLabel != null) {
            mainTitleLabel.setText("Organization Information");
        }

        if (mainSubtitleLabel != null) {
            mainSubtitleLabel.setText("View organization details and contact information.");
        }
    }
}
