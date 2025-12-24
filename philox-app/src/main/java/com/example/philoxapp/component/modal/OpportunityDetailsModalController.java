package com.example.philoxapp.component.modal;

import com.example.philoxapp.organisationScreens.OrganisationProfileController;
import entity.Opportunity;
import entity.Organisation;
import entity.Volunteer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import service.ApplicationService;
import session.Session;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;

public class OpportunityDetailsModalController {

    Volunteer volunteer;
    Window ownerWindow;

    public void showDetailsModal(Volunteer volunteer,Opportunity opportunity, Window ownerWindow) {
        // Convert opportunity to info object
        OpportunityInfo info = convertOpportunityToInfo(opportunity);
        this.volunteer = volunteer;

        // Build modal content
        VBox root = createModalContent(info, ownerWindow, opportunity);
        this.ownerWindow = ownerWindow;

        // Create and show the modal
        showModal(root, ownerWindow);
    }

    private OpportunityInfo convertOpportunityToInfo(Opportunity opportunity) {
        // Format date range
        String dateRange = "";
        if (opportunity.getStartDate() != null && opportunity.getEndDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            dateRange = opportunity.getStartDate().format(formatter) + " - " +
                       opportunity.getEndDate().format(formatter);
        } else if (opportunity.getStartDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            dateRange = opportunity.getStartDate().format(formatter);
        }

        // name and mission
        String orgName = "";
        String orgMission = "";
        if (opportunity.getOrganisation() != null) {
            orgName = opportunity.getOrganisation().getName();
            orgMission = opportunity.getOrganisation().getMission() != null ? opportunity.getOrganisation().getMission() : "";
        }

        return new OpportunityInfo(
            opportunity.getTitle() != null ? opportunity.getTitle() : "",
            orgName, orgMission, opportunity.getDescription() != null ? opportunity.getDescription() : "",
            opportunity.getLocation() != null ? opportunity.getLocation() : "",
            dateRange
        );
    }

    private VBox createModalContent(OpportunityInfo info, Window ownerWindow, Opportunity opportunity) {
        VBox root = new VBox();
        root.setSpacing(14);
        root.setStyle("-fx-background-color: white; -fx-padding: 22; -fx-background-radius: 10; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12,0,0,4);");
        root.setMaxWidth(820);
        root.setMaxHeight(350);

        // Top row with close button
        HBox topRow = createTopRow();

        // Title
        Label titleLabel = new Label(info.title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: #274C56;");

        // Meta row (org and location with icons)
        HBox metaRow = createMetaRow(info);

        // Date row with calendar emoji
        HBox dateRow = createDateRow(info);

        // About sections
        Label aboutTitle = new Label("About this Opportunity");
        aboutTitle.setStyle("-fx-font-weight: 700; -fx-text-fill: #274C56;");
        Label aboutDesc = new Label(info.description);
        aboutDesc.setWrapText(true);
        aboutDesc.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");

        Label aboutOrgTitle = new Label("About " + (info.orgName.isEmpty() ? "Organisation" : info.orgName));
        aboutOrgTitle.setStyle("-fx-font-weight: 700; -fx-text-fill: #274C56;");
        Label aboutOrgDesc = new Label(info.orgMission.isEmpty() ? "" : info.orgMission);
        aboutOrgDesc.setWrapText(true);
        aboutOrgDesc.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");

        Separator sep = new Separator();

        // Bottom row with profile link and apply button
        HBox bottomRow = createBottomRow(info, ownerWindow, opportunity);

        // Assemble all components
        root.getChildren().addAll(topRow, titleLabel, metaRow, dateRow, aboutTitle, aboutDesc, aboutOrgTitle, aboutOrgDesc, sep, bottomRow);

        return root;
    }

    private HBox createTopRow() {
        HBox topRow = new HBox();
        topRow.setSpacing(5);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #1f2937;");
        closeBtn.setOnAction(e -> ((Stage) closeBtn.getScene().getWindow()).close());
        topRow.getChildren().addAll(spacer, closeBtn);
        return topRow;
    }

    private HBox createMetaRow(OpportunityInfo info) {
        HBox metaRow = new HBox();
        metaRow.setSpacing(10);

        // Org with icon
        HBox orgBox = new HBox();
        orgBox.setSpacing(8);
        ImageView orgIcon = new ImageView();
        orgIcon.setFitWidth(16);
        orgIcon.setFitHeight(16);
        loadIconIntoView(orgIcon, "/assets/orgLabelIcon.png");
        Label orgText = new Label(info.orgName);
        orgText.setStyle("-fx-text-fill: #4A5565;");
        orgBox.getChildren().addAll(orgIcon, orgText);

        // Location with icon
        HBox locBox = new HBox();
        locBox.setSpacing(8);
        ImageView locIcon = new ImageView();
        locIcon.setFitWidth(16);
        locIcon.setFitHeight(16);
        loadIconIntoView(locIcon, "/assets/locationIcon.png");
        Label locText = new Label(info.location);
        locText.setStyle("-fx-text-fill: #4A5565;");
        locBox.getChildren().addAll(locIcon, locText);

        metaRow.getChildren().addAll(orgBox, locBox);
        return metaRow;
    }

    private HBox createDateRow(OpportunityInfo info) {
        HBox dateRow = new HBox();
        dateRow.setSpacing(8);
        Label calEmoji = new Label("📅");
        calEmoji.setStyle("-fx-font-size: 14px;");
        Label dateText = new Label(info.date);
        dateText.setStyle("-fx-text-fill: #4A5565;");
        dateRow.getChildren().addAll(calEmoji, dateText);
        return dateRow;
    }

    private HBox createBottomRow(OpportunityInfo info, Window ownerWindow, Opportunity opportunity) {
        HBox bottomRow = new HBox();
        bottomRow.setSpacing(12);
        bottomRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Hyperlink profileLink = new Hyperlink("See full organisation profile");
        profileLink.setOnAction(e -> {
           handleViewOrganization(opportunity.getOrganisation());
        });

        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        Button applyBtn = new Button("Apply Now");

        // Check if the volunteer has already applied to this opportunity
        boolean hasAlreadyApplied = false;
        try {
            Session session = Session.getSession();
            if (session.getCurrentUser() instanceof Volunteer) {
                Volunteer volunteer = (Volunteer) session.getCurrentUser();
                hasAlreadyApplied = ApplicationService.hasVolunteerApplied(volunteer.getVolunteerId(), opportunity.getOpportunityId());
            }
        } catch (Exception ex) {
            // If there's an error checking, assume not applied to allow the user to try
            hasAlreadyApplied = false;
        }

        if (hasAlreadyApplied) {
            applyBtn.setText("Already Applied");
            applyBtn.setDisable(true);
            applyBtn.setStyle("-fx-background-color: #9CA3AF; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 22;");
        } else {
            applyBtn.setStyle("-fx-background-color: #04937a; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 22;");
        }

        boolean finalHasAlreadyApplied = hasAlreadyApplied;
        applyBtn.setOnAction(e -> {
            if (!finalHasAlreadyApplied) {
                // Create and show apply modal using showAndWait pattern like RatingModal
                ApplyModalController applyController = new ApplyModalController(opportunity);
                boolean applicationSuccessful = applyController.showAndWait();

                // Only disable the button if the application was successful
                if (applicationSuccessful) {
                    applyBtn.setText("Already Applied");
                    applyBtn.setDisable(true);
                    applyBtn.setStyle("-fx-background-color: #9CA3AF; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 22;");
                }
                // If application failed, button remains enabled so user can try again
            }
        });

        bottomRow.getChildren().addAll(profileLink, bottomSpacer, applyBtn);
        return bottomRow;
    }

    private void handleViewOrganization(Organisation organisation) {
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
            //Stage currentStage = (Stage)ownerWindow;
            stage.initOwner(ownerWindow);
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

    private void showModal(VBox root, Window ownerWindow) {
        // Create modal stage
        Stage dialog = new Stage(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.APPLICATION_MODAL);
        if (ownerWindow != null) {
            dialog.initOwner(ownerWindow);
        }

        // Create overlay wrapper with semi-transparent black background
        StackPane wrapper = new StackPane();
        wrapper.setStyle("-fx-background-color: rgba(0,0,0,0.25);");
        wrapper.getChildren().add(root);
        StackPane.setAlignment(root, Pos.CENTER);

        // Size and position dialog to cover owner window
        double ow = ownerWindow != null ? ownerWindow.getWidth() : 800;
        double oh = ownerWindow != null ? ownerWindow.getHeight() : 600;
        Scene scene = new Scene(wrapper, ow, oh, Color.TRANSPARENT);

        // Place dialog exactly over owner
        if (ownerWindow != null) {
            dialog.setX(ownerWindow.getX());
            dialog.setY(ownerWindow.getY());
        }

        dialog.setScene(scene);
        dialog.show();
    }



    private void loadIconIntoView(ImageView view, String path) {
        if (view == null) return;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) {
                view.setImage(new Image(is));
            } else {
                // silently ignore if asset missing; icon is optional
            }
        } catch (Exception e) {
            // ignore
        }
    }

    // Data class to hold opportunity information
    private static class OpportunityInfo {
        final String title;
        final String orgName;
        final String orgMission;
        final String description;
        final String location;
        final String date;

        OpportunityInfo(String title, String orgName, String orgMission, String description, String location, String date) {
            this.title = title;
            this.orgName = orgName;
            this.orgMission = orgMission;
            this.description = description;
            this.location = location;
            this.date = date;
        }
    }
}
