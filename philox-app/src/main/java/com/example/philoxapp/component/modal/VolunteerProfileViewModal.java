package com.example.philoxapp.component.modal;

import com.example.philoxapp.organisationScreens.ApplicationsController;
import com.example.philoxapp.volunteerScreens.VolunteerProfileController;
import entity.Application;
import entity.Volunteer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.ApplicationService;

import java.io.IOException;

import service.RatingService;
import model.ApplicationStatus;

import java.util.Optional;

public class VolunteerProfileViewModal {

    @FXML private VBox rootPane;
    @FXML private Button closeButton;
    @FXML private Label nameLabel;
    @FXML private Label opportunityLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label cityLabel;
    @FXML private Label appliedDateLabel;
    @FXML private Label skillsLabel;
    @FXML private HBox acceptRejectBox;
    @FXML private HBox rateBox;

    @FXML private Button acceptButton;
    @FXML private Button rejectButton;
    @FXML private Button rateButton;

    @FXML private TextArea applicationMessageArea;

    private ApplicationsController.ApplicationData application;
    private Stage stage;
    private entity.Organisation organisation; // Store organization context for rating


    @FXML
    public void initialize() { }

    public void setOrganisation(entity.Organisation org) {
        this.organisation = org;
    }

    public void setApplication(ApplicationsController.ApplicationData app) {
        this.application = app;

        nameLabel.setText(app.getVolunteerName());
        opportunityLabel.setText("Applied for: " + app.getOpportunityName());
        emailLabel.setText(app.getVolunteerEmail());
        phoneLabel.setText("+92 300 1234567"); // Mock data
        cityLabel.setText(app.getVolunteerCity());
        appliedDateLabel.setText(app.getAppliedDate());
        skillsLabel.setText(app.getSkills());
        applicationMessageArea.setText(app.getApplicationMessage());

        // Show different UI elements based on application status
        if (app.getStatus() == ApplicationStatus.PENDING) {
            // Show accept/reject buttons for pending applications
            acceptRejectBox.setVisible(true);
            acceptRejectBox.setManaged(true);
            rateBox.setVisible(false);
            rateBox.setManaged(false);
        } else if (app.getStatus() == ApplicationStatus.FINALISED) {
            // Show rate button for finalised applications
            acceptRejectBox.setVisible(false);
            acceptRejectBox.setManaged(false);
            rateBox.setVisible(true);
            rateBox.setManaged(true);

            // Check if organization has already rated this volunteer
            int orgId = getOrganizationId();
            if (orgId != -1 && app.getApplication() != null && app.getApplication().getVolunteer() != null) {
                System.out.println("Checking if org " + orgId + " has rated volunteer " + app.getApplication().getVolunteer().getVolunteerId());
                boolean hasAlreadyRated = RatingService.hasUserRatedUser(
                    orgId,
                    app.getApplication().getVolunteer().getVolunteerId()
                );
                System.out.println("Has already rated: " + hasAlreadyRated);

                if (hasAlreadyRated) {
                    rateButton.setText("Already Rated");
                    rateButton.setDisable(true);
                    rateButton.setStyle("-fx-background-color: #9CA3AF; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 25;");
                } else {
                    rateButton.setText("★ Rate Volunteer");
                    rateButton.setDisable(false);
                    rateButton.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 25; -fx-cursor: hand;");
                }
            }
        } else {
            // Hide both for other statuses (accepted, rejected)
            acceptRejectBox.setVisible(false);
            acceptRejectBox.setManaged(false);
            rateBox.setVisible(false);
            rateBox.setManaged(false);
        }
    }

    @FXML
    private void handleAccept() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Accept Application");
        alert.setHeaderText("Accept " + application.getVolunteerName() + "'s application?");
        alert.setContentText("They will be notified via email.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Application acceptedApp = application.getApplication();
            acceptedApp.setStatus(ApplicationStatus.ACCEPTED.getCode());
            boolean acceptedSuccess = ApplicationService.updateApplicationStatus(acceptedApp);

            if (acceptedSuccess) {
                application.setStatus(ApplicationStatus.ACCEPTED);
                handleClose();
            } else {
                showError("Failed to accept application. Please try again.");
            }
        }
    }

    @FXML
    private void handleReject() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reject Application");
        alert.setHeaderText("Reject " + application.getVolunteerName() + "'s application?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Application rejectedApp = application.getApplication();
            rejectedApp.setStatus(ApplicationStatus.REJECTED.getCode());
            boolean rejectedSuccess = ApplicationService.updateApplicationStatus(rejectedApp);

            if (rejectedSuccess) {
                application.setStatus(ApplicationStatus.REJECTED);
                handleClose();
            } else {
                showError("Failed to reject application. Please try again.");
            }
        }
    }

    @FXML
    private void handleOpenRatingPopup() {
        if (application == null || application.getApplication() == null || application.getApplication().getVolunteer() == null) {
            showError("Cannot rate: volunteer information not available.");
            return;
        }

        int orgId = getOrganizationId();
        if (orgId == -1) {
            showError("Cannot rate: organization context not available.");
            return;
        }

        // Get the current window as owner
        Stage owner = (Stage) closeButton.getScene().getWindow();

        // Create and show the rating modal for organization rating volunteer
        RatingModal ratingModal = new RatingModal(
            RatingModal.RatingType.ORGANIZATION_TO_VOLUNTEER,
            orgId,
            application.getApplication().getVolunteer().getVolunteerId(),
            application.getVolunteerName(),
            owner
        );

        boolean ratingSubmitted = ratingModal.showAndWait();

        // Refresh the button state after rating attempt - only if rating was submitted
        if (ratingSubmitted && application != null) {
            setApplication(application); // Refresh the UI state
        }
    }

    // Helper method to get organization ID from context
    private int getOrganizationId() {
        if (organisation != null) {
            return organisation.getOrganisationId();
        }
        // Fallback: try to get from application context
        if (application != null && application.getApplication() != null &&
            application.getApplication().getOpportunity() != null &&
            application.getApplication().getOpportunity().getOrganisation() != null) {
            return application.getApplication().getOpportunity().getOrganisation().getOrganisationId();
        }
        return -1; // Invalid ID if no organization context available
    }

    @FXML
    private void handleViewCompleteProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/volunteerProfile.fxml"));
            Parent root = loader.load();

            VolunteerProfileController controller = loader.getController();
            Volunteer volunteer = application.getApplication().getVolunteer();
            controller.setVolunteer(volunteer, VolunteerProfileController.ViewMode.ORGANIZATION_VIEW);

            Stage owner = (Stage) rootPane.getScene().getWindow();
            Stage dialog = new Stage();
            dialog.setTitle("Volunteer Profile");
            dialog.initOwner(owner);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setScene(new Scene(root));
            dialog.show();
        } catch (IOException ex) {
            showError("Failed to load volunteer profile view.");
        }
    }


    @FXML
    private void handleClose() {
        stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}