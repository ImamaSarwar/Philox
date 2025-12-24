package com.example.philoxapp.volunteerScreens;

import com.example.philoxapp.component.sidepanel.SidePanel;
import com.example.philoxapp.component.modal.RatingModal;
import com.example.philoxapp.organisationScreens.OrganisationProfileController;
import service.ApplicationService;
import entity.Application;
import entity.Volunteer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.ApplicationStatus;
import java.io.InputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class VolunteerApplicationTrackingController implements Initializable {

    @FXML private VBox sidebarContainer;
    @FXML private ComboBox<String> statusFilterComboBox;

    // table row labels / actions
    @FXML private Label opp1Label; @FXML private Label opp2Label; @FXML private Label opp3Label; @FXML private Label opp4Label; @FXML private Label opp5Label;
    @FXML private Label org1Label; @FXML private Label org2Label; @FXML private Label org3Label; @FXML private Label org4Label; @FXML private Label org5Label;
    @FXML private Label date1Label; @FXML private Label date2Label; @FXML private Label date3Label; @FXML private Label date4Label; @FXML private Label date5Label;
    @FXML private Label status1Label; @FXML private Label status2Label; @FXML private Label status3Label; @FXML private Label status4Label; @FXML private Label status5Label;
    @FXML private Hyperlink view1Link; @FXML private Hyperlink view2Link; @FXML private Hyperlink view3Link; @FXML private Hyperlink view4Link; @FXML private Hyperlink view5Link;

    // popup controls (matches latest FXML)
    @FXML private AnchorPane overlayPane;
    @FXML private Label popupOppTitle;
    @FXML private Hyperlink popupOrg;
    @FXML private Label popupLocation;
    @FXML private Label popupDates;
    @FXML private Label popupStatusBadge;
    @FXML private Label popupDescription;
    @FXML private Button closePopupButton;
    @FXML private Button rateOrgButton; // new button
    @FXML private ImageView popupLocationIcon, popupOrgIcon, popupCalendarIcon;
    // convenience lists for indexing
    private List<Label> oppLabels;
    private List<Label> orgLabels;
    private List<Label> dateLabels;
    private List<Label> statusLabels;
    private List<Hyperlink> viewLinks;
    private List<Application> applications; // Store loaded applications
    private List<Application> allApplications; // Store all applications for filtering
    private Application currentRatingApplication; // Track current app for rating

    Volunteer volunteer;

    public void setVolunteer(Volunteer v) {
        this.volunteer = v;

        // Setup sidebar with new SidePanel system
        SidePanel sidePanel = new SidePanel(volunteer, "My Applications");
        sidebarContainer.getChildren().clear();
        sidebarContainer.getChildren().add(sidePanel.getNode());

        // Load applications from database
        loadApplicationsData();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // hide overlay initially
        if (overlayPane != null) overlayPane.setVisible(false);
        loadIcon(popupLocationIcon, "/assets/locationIcon.png");
        loadIcon(popupOrgIcon, "/assets/building.png");
        loadIcon(popupCalendarIcon, "/assets/calendar.png");
        // ensure rate button hidden by default (no layout gap)
        if (rateOrgButton != null) {
            rateOrgButton.setVisible(false);
            rateOrgButton.setManaged(false);
            rateOrgButton.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 18; -fx-font-size: 14px;");
            rateOrgButton.setOnAction(evt -> onRateOrganisation());
        }

        // prepare lists (assume FXML injection succeeded)
        oppLabels = Arrays.asList(opp1Label, opp2Label, opp3Label, opp4Label, opp5Label);
        orgLabels = Arrays.asList(org1Label, org2Label, org3Label, org4Label, org5Label);
        dateLabels = Arrays.asList(date1Label, date2Label, date3Label, date4Label, date5Label);
        statusLabels = Arrays.asList(status1Label, status2Label, status3Label, status4Label, status5Label);
        viewLinks = Arrays.asList(view1Link, view2Link, view3Link, view4Link, view5Link);

        // Initialize status filter combo box
        if (statusFilterComboBox != null) {
            statusFilterComboBox.getItems().add("All Applications");
            for (ApplicationStatus status : ApplicationStatus.values()) {
                statusFilterComboBox.getItems().add(status.toString());
            }
            statusFilterComboBox.setValue("All Applications");
            statusFilterComboBox.setOnAction(e -> filterApplications());
        }

        // Data will be loaded when volunteer is set
        clearAllLabels();

        if (closePopupButton != null) closePopupButton.setOnAction(e -> closeApplicationPopup());
    }

    private void applyStatusStyle(Label label, String status) {
        String base = "-fx-padding: 4 8; -fx-background-radius: 12; -fx-font-size: 12px;";
        if (status == null) {
            label.setStyle(base + "-fx-background-color: #F3F4F6; -fx-text-fill: #374151;");
            return;
        }
        switch (status.trim().toLowerCase()) {
            case "accepted":
            case "approved":
                label.setStyle(base + "-fx-background-color: #E6FFFA; -fx-text-fill: #059669;");
                break;
            case "pending":
                label.setStyle(base + "-fx-background-color: #FFFBEB; -fx-text-fill: #B45309;");
                break;
            case "rejected":
                label.setStyle(base + "-fx-background-color: #FFF1F2; -fx-text-fill: #BE123C;");
                break;
            case "finalised":
            case "finalized":
                label.setStyle(base + "-fx-background-color: #E6EEFF; -fx-text-fill: #3B82F6;");
                break;
            case "withdrawn":
                label.setStyle(base + "-fx-background-color: #FEF2F2; -fx-text-fill: #DC2626;");
                break;
            default:
                label.setStyle(base + "-fx-background-color: #F3F4F6; -fx-text-fill: #374151;");
        }
    }

    private void loadIcon(ImageView view, String path) {
        if (view == null) return;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) {
                Image img = new Image(is);
                view.setImage(img);
            } else {
                System.err.println("Resource " + path + " not found on classpath.");
            }
        } catch (Exception e) {
            System.err.println("Error loading icon " + path + ": " + e.getMessage());
        }
    }

    // invoked by link handlers; always shows unified popup (pending/rejected/accepted)
    private void openDetailsForRow(int index) {
        if (applications == null || index < 0 || index >= applications.size()) {
            return; // No data available for this row
        }

        Application app = applications.get(index);
        String opp = app.getOpportunity() != null ? app.getOpportunity().getTitle() : "Unknown Opportunity";
        String org = (app.getOpportunity() != null && app.getOpportunity().getOrganisation() != null)
                    ? app.getOpportunity().getOrganisation().getName() : "Unknown Organization";
        String status = ApplicationStatus.fromCode(app.getStatus()).toString();

        // Show popup with real application data
        showApplicationDetailsWithData(app, opp, org, status);
    }

    private String safeGet(List<Label> list, int idx) {
        if (list == null || idx < 0 || idx >= list.size()) return "";
        Label l = list.get(idx);
        return l != null && l.getText() != null ? l.getText().trim() : "";
    }

    // fills popup controls with real application data and shows overlay
    public void showApplicationDetailsWithData(Application app, String opportunityTitle, String organisation, String status) {
        if (popupOppTitle != null) popupOppTitle.setText(opportunityTitle != null && !opportunityTitle.isBlank() ? opportunityTitle : "Opportunity Title");

        if (popupOrg != null) {
            popupOrg.setText(organisation != null && !organisation.isBlank() ? organisation : "Organisation Name");
            // Set up click handler to view organization profile
            if (app != null && app.getOpportunity() != null && app.getOpportunity().getOrganisation() != null) {
                popupOrg.setOnAction(e -> handleViewOrganization(app.getOpportunity().getOrganisation()));
                popupOrg.setStyle("-fx-text-fill: #007bff; -fx-underline: true; -fx-font-size: 14px; -fx-cursor: hand;");
            } else {
                popupOrg.setOnAction(null);
                popupOrg.setStyle("-fx-text-fill: #333; -fx-font-size: 14px;");
            }
        }

        // Use real data from application
        if (popupLocation != null && app.getOpportunity() != null) {
            popupLocation.setText(app.getOpportunity().getLocation() != null ? app.getOpportunity().getLocation() : "Location not specified");
        }

        if (popupDates != null && app.getOpportunity() != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            String startDate = app.getOpportunity().getStartDate() != null ? app.getOpportunity().getStartDate().format(dateFormatter) : "TBD";
            String endDate = app.getOpportunity().getEndDate() != null ? app.getOpportunity().getEndDate().format(dateFormatter) : "TBD";
            popupDates.setText(startDate + " - " + endDate);
        }

        if (popupDescription != null && app.getOpportunity() != null) {
            popupDescription.setText(app.getOpportunity().getDescription() != null ? app.getOpportunity().getDescription() : "No description available");
        }

        // status badge text + style
        String s = status == null ? "Pending" : status;
        if (popupStatusBadge != null) {
            popupStatusBadge.setText(s);
            switch (s.trim().toLowerCase()) {
                case "accepted":
                case "approved":
                    popupStatusBadge.setStyle("-fx-background-color: #E6FFFA; -fx-text-fill: #009689; -fx-padding: 6 10; -fx-background-radius: 12; -fx-font-size: 12px;");
                    break;
                case "rejected":
                case "declined":
                    popupStatusBadge.setStyle("-fx-background-color: #FFF5F5; -fx-text-fill: #C53030; -fx-padding: 6 10; -fx-background-radius: 12; -fx-font-size: 12px;");
                    break;
                case "finalised":
                case "finalized":
                    popupStatusBadge.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1447E6; -fx-padding: 6 10; -fx-background-radius: 12; -fx-font-size: 12px;");
                    break;
                case "withdrawn":
                    popupStatusBadge.setStyle("-fx-background-color: #FEF2F2; -fx-text-fill: #DC2626; -fx-padding: 6 10; -fx-background-radius: 12; -fx-font-size: 12px;");
                    break;
                default:
                    popupStatusBadge.setStyle("-fx-background-color: #FFFBEB; -fx-text-fill: #B45309; -fx-padding: 6 10; -fx-background-radius: 12; -fx-font-size: 12px;");
            }
        }

        // Store current application for rating
        this.currentRatingApplication = app;

        // show/hide rate button only for finalised/finalized
        boolean showRate = s != null && (s.trim().equalsIgnoreCase("finalised") || s.trim().equalsIgnoreCase("finalized"));
        if (rateOrgButton != null) {
            rateOrgButton.setVisible(showRate);
            rateOrgButton.setManaged(showRate);

            // Check if volunteer has already rated this organization and disable button if so
            if (showRate && app != null && app.getOpportunity() != null && app.getOpportunity().getOrganisation() != null) {
                boolean hasAlreadyRated = service.RatingService.hasUserRatedUser(
                    volunteer.getVolunteerId(),
                    app.getOpportunity().getOrganisation().getOrganisationId()
                );

                if (hasAlreadyRated) {
                    rateOrgButton.setText("Already Rated");
                    rateOrgButton.setDisable(true);
                    rateOrgButton.setStyle("-fx-background-color: #9CA3AF; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 18; -fx-font-size: 14px;");
                } else {
                    rateOrgButton.setText("Rate Organization");
                    rateOrgButton.setDisable(false);
                    rateOrgButton.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 18; -fx-font-size: 14px;");
                }
            }
        }

        if (overlayPane != null) overlayPane.setVisible(true);
    }

    // fills popup controls and shows overlay (legacy method for backward compatibility)
    public void showApplicationDetails(String opportunityTitle, String organisation, String status) {
        if (popupOppTitle != null) popupOppTitle.setText(opportunityTitle != null && !opportunityTitle.isBlank() ? opportunityTitle : "Opportunity Title");

        if (popupOrg != null) {
            popupOrg.setText(organisation != null && !organisation.isBlank() ? organisation : "Organisation Name");
            // Legacy method - no click handler for organization
            popupOrg.setOnAction(null);
            popupOrg.setStyle("-fx-text-fill: #333; -fx-font-size: 14px;");
        }

        // dummy values for now; replace with backend data later
        if (popupLocation != null) popupLocation.setText("Karachi");
        if (popupDates != null) popupDates.setText("Nov 20 - Nov 25, 2025");
        if (popupDescription != null) popupDescription.setText("Help distribute food packages to underprivileged families in the community. Volunteers will assist with packing and door-to-door distribution.");

        // status badge text + style
        String s = status == null ? "Pending" : status;
        if (popupStatusBadge != null) {
            popupStatusBadge.setText(s);
            switch (s.trim().toLowerCase()) {
                case "accepted":
                case "approved":
                    popupStatusBadge.setStyle("-fx-background-color: #E6FFFA; -fx-text-fill: #009689; -fx-padding: 6 10; -fx-background-radius: 12; -fx-font-size: 12px;");
                    break;
                case "rejected":
                case "declined":
                    popupStatusBadge.setStyle("-fx-background-color: #FFF5F5; -fx-text-fill: #C53030; -fx-padding: 6 10; -fx-background-radius: 12; -fx-font-size: 12px;");
                    break;
                case "finalised":
                case "finalized":
                    // updated per request: background #DBEAFE and text #1447E6
                    popupStatusBadge.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1447E6; -fx-padding: 6 10; -fx-background-radius: 12; -fx-font-size: 12px;");
                    break;
                case "withdrawn":
                    popupStatusBadge.setStyle("-fx-background-color: #FEF2F2; -fx-text-fill: #DC2626; -fx-padding: 6 10; -fx-background-radius: 12; -fx-font-size: 12px;");
                    break;
                default:
                    popupStatusBadge.setStyle("-fx-background-color: #FFFBEB; -fx-text-fill: #B45309; -fx-padding: 6 10; -fx-background-radius: 12; -fx-font-size: 12px;");
            }
        }

        // show/hide rate button only for finalised/finalized
        boolean showRate = s != null && (s.trim().equalsIgnoreCase("finalised") || s.trim().equalsIgnoreCase("finalized"));
        if (rateOrgButton != null) {
            rateOrgButton.setVisible(showRate);
            rateOrgButton.setManaged(showRate);
        }

        if (overlayPane != null) overlayPane.setVisible(true);
    }

    @FXML
    private void closeApplicationPopup() {
        if (overlayPane != null) overlayPane.setVisible(false);
    }

    private void loadApplicationsData() {
        if (volunteer == null) return;
        try {
            allApplications = ApplicationService.getApplicationsForVolunteer(volunteer.getVolunteerId());
            applications = new ArrayList<>(allApplications); // Initialize with all applications
            refreshDisplayedApplications();
        } catch (Exception e) {
            e.printStackTrace();
            clearAllLabels();
        }
    }

    private void clearAllLabels() {
        // Clear all labels to show empty state
        for (Label label : oppLabels) {
            if (label != null) label.setText("");
        }
        for (Label label : orgLabels) {
            if (label != null) label.setText("");
        }
        for (Label label : dateLabels) {
            if (label != null) label.setText("");
        }
        for (Label label : statusLabels) {
            if (label != null) {
                label.setText("");
                label.setStyle(""); // Clear any status styling
            }
        }
        for (Hyperlink link : viewLinks) {
            if (link != null) {
                link.setOnAction(null); // Clear any action handlers
            }
        }
    }

    private void handleViewOrganization(entity.Organisation organisation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/organisationProfile.fxml"));
            Parent root = loader.load();

            OrganisationProfileController controller = loader.getController();
            // Pass the volunteer as the viewer and set to VOLUNTEER_VIEW mode
            controller.setOrganisation(organisation, volunteer, OrganisationProfileController.ViewMode.VOLUNTEER_VIEW);

            Stage stage = new Stage();
            stage.setTitle(organisation.getName() + " - Profile");
            stage.setScene(new Scene(root, 900, 700)); // Set smaller window size
            stage.setResizable(true);
            stage.setMinWidth(800);
            stage.setMinHeight(600);

            // Set the stage to be modal to the current window
            Stage currentStage = (Stage) overlayPane.getScene().getWindow();
            stage.initOwner(currentStage);
            stage.initModality(Modality.WINDOW_MODAL);

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load organization profile: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void onRateOrganisation() {
        if (currentRatingApplication == null ||
            currentRatingApplication.getOpportunity() == null ||
            currentRatingApplication.getOpportunity().getOrganisation() == null) {
            return; // No valid application to rate
        }

        // Hide the current overlay
        if (overlayPane != null) {
            overlayPane.setVisible(false);
        }

        // Get window owner for modal
        Window owner = (overlayPane != null && overlayPane.getScene() != null) ?
            overlayPane.getScene().getWindow() : null;

        // Create and show rating modal
        RatingModal ratingModal = new RatingModal(
            RatingModal.RatingType.VOLUNTEER_TO_ORGANIZATION,
            volunteer.getVolunteerId(),
            currentRatingApplication.getOpportunity().getOrganisation().getOrganisationId(),
            currentRatingApplication.getOpportunity().getOrganisation().getName(),
            owner
        );

        ratingModal.showAndWait();

        // Refresh the button state after rating attempt
        if (currentRatingApplication != null) {
            String oppTitle = currentRatingApplication.getOpportunity().getTitle();
            String orgName = currentRatingApplication.getOpportunity().getOrganisation().getName();
            String status = ApplicationStatus.fromCode(currentRatingApplication.getStatus()).toString();
            showApplicationDetailsWithData(currentRatingApplication, oppTitle, orgName, status);
        }
    }

    private void filterApplications() {
        if (allApplications == null || statusFilterComboBox == null) return;

        String selectedStatus = statusFilterComboBox.getValue();
        if ("All Applications".equals(selectedStatus)) {
            applications = new ArrayList<>(allApplications);
        } else {
            // Find the ApplicationStatus enum that matches the selected string
            ApplicationStatus filterStatus = null;
            for (ApplicationStatus status : ApplicationStatus.values()) {
                if (status.toString().equals(selectedStatus)) {
                    filterStatus = status;
                    break;
                }
            }

            if (filterStatus != null) {
                final ApplicationStatus finalFilterStatus = filterStatus;
                applications = allApplications.stream()
                    .filter(app -> ApplicationStatus.fromCode(app.getStatus()) == finalFilterStatus)
                    .collect(java.util.stream.Collectors.toList());
            } else {
                applications = new ArrayList<>(allApplications);
            }
        }

        refreshDisplayedApplications();
    }

    private void refreshDisplayedApplications() {
        clearAllLabels();
        if (applications == null) return;

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        int appCount = applications.size();
        int maxItems = Math.min(appCount, oppLabels.size());

        for (int i = 0; i < oppLabels.size(); i++) {
            if (i < appCount) {
                Application app = applications.get(i);
                // Set opportunity title
                if (oppLabels.get(i) != null && app.getOpportunity() != null) {
                    oppLabels.get(i).setText(" " + app.getOpportunity().getTitle());
                    oppLabels.get(i).setVisible(true);
                    oppLabels.get(i).setManaged(true);
                }
                // Set organization name
                if (orgLabels.get(i) != null && app.getOpportunity() != null && app.getOpportunity().getOrganisation() != null) {
                    orgLabels.get(i).setText(app.getOpportunity().getOrganisation().getName());
                    orgLabels.get(i).setVisible(true);
                    orgLabels.get(i).setManaged(true);
                }
                // Set application date
                if (dateLabels.get(i) != null && app.getApplicationDate() != null) {
                    String formattedDate = app.getApplicationDate().toLocalDate().format(dateFormatter);
                    dateLabels.get(i).setText("📅  " + formattedDate);
                    dateLabels.get(i).setVisible(true);
                    dateLabels.get(i).setManaged(true);
                }
                // Set status
                if (statusLabels.get(i) != null) {
                    ApplicationStatus status = ApplicationStatus.fromCode(app.getStatus());
                    String statusText = status.toString();
                    statusLabels.get(i).setText(statusText);
                    applyStatusStyle(statusLabels.get(i), statusText);
                    statusLabels.get(i).setVisible(true);
                    statusLabels.get(i).setManaged(true);
                }
                // Set view link action
                if (viewLinks.get(i) != null) {
                    final int idx = i;
                    viewLinks.get(i).setOnAction(evt -> openDetailsForRow(idx));
                    viewLinks.get(i).setVisible(true);
                    viewLinks.get(i).setManaged(true);
                }
            } else {
                // Hide unused row
                if (oppLabels.get(i) != null) { oppLabels.get(i).setText(""); oppLabels.get(i).setVisible(false); oppLabels.get(i).setManaged(false); }
                if (orgLabels.get(i) != null) { orgLabels.get(i).setText(""); orgLabels.get(i).setVisible(false); orgLabels.get(i).setManaged(false); }
                if (dateLabels.get(i) != null) { dateLabels.get(i).setText(""); dateLabels.get(i).setVisible(false); dateLabels.get(i).setManaged(false); }
                if (statusLabels.get(i) != null) { statusLabels.get(i).setText(""); statusLabels.get(i).setVisible(false); statusLabels.get(i).setManaged(false); }
                if (viewLinks.get(i) != null) { viewLinks.get(i).setOnAction(null); viewLinks.get(i).setVisible(false); viewLinks.get(i).setManaged(false); }
            }
        }
    }

}
