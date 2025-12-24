package com.example.philoxapp.volunteerScreens;

import com.example.philoxapp.UserProfileController;
import entity.Opportunity;
import entity.Rating;
import entity.Volunteer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.philoxapp.component.sidepanel.SidePanel;
import com.example.philoxapp.component.card.RatingCard;
import service.ApplicationService;
import service.ProfileService;
import service.RatingService;


public class VolunteerProfileController extends UserProfileController {
    /*
    public static class Rating {
        private int stars;           // 1–5
        private String comment;      // review text
        private String orgName;      // e.g., "Edhi Foundation"
        private String date;         // e.g., "Nov 2025"

        public Rating(int stars, String comment, String orgName, String date) {
            this.stars = stars;
            this.comment = comment;
            this.orgName = orgName;
            this.date = date;
        }

        public int getStars() { return stars; }
        public String getComment() { return comment; }
        public String getOrgName() { return orgName; }
        public String getDate() { return date; }
    }
     */
    // View mode enum
    public enum ViewMode {
        VOLUNTEER_SELF_VIEW,  // Volunteer viewing their own profile (full editing)
        ORGANIZATION_VIEW     // Organization viewing volunteer profile (read-only)
    }

    private ViewMode currentViewMode = ViewMode.VOLUNTEER_SELF_VIEW;

    // ----------------------------------------------------------------------
    //                 EXISTING PROFILE FXML IDs — UNTOUCHED
    // ----------------------------------------------------------------------

    @FXML private BorderPane mainBorderPane;
    @FXML private VBox sidebarContainer;
    @FXML private StackPane contentContainer;

    @FXML private Label initialsLabel;
    @FXML private Label nameLabel, emailLabel, phoneLabel, cityLabel;
    @FXML private Label skillsLabel;
    @FXML private TextField fullNameField, emailField, phoneField, cnicField, cityField, ageField;
    @FXML private Button personalInfoTab, portfolioTab, achievementsTab, editButton;
    @FXML private VBox viewGridContainer;
    @FXML private ImageView starBadgeImage;
    @FXML private ImageView ecoBadgeImage;
    @FXML private ImageView lockBadgeImage;
    @FXML private ImageView tipIcon;
    @FXML private ImageView summaryEmailIcon, phoneIcon, locationIcon;
    @FXML private ImageView fullNameIcon, emailIcon, formPhoneIcon, cnicIcon, cityIcon, ageIcon, editIcon;
    @FXML private FlowPane ratingsContainer;
    @FXML private Label averageRatingLabel;

    // Header elements for dynamic update
    @FXML private Label headerTitleLabel;
    @FXML private Label headerSubtitleLabel;

    // ----------------------------------------------------------------------
    //                 PORTFOLIO FXML IDs — ADDED SAFELY
    // ----------------------------------------------------------------------

    @FXML private Button portfolioEditButton;
    @FXML private ImageView portfolioEditIcon;
    @FXML private Label bioLabel;
    @FXML private TextField bioField;

    @FXML private VBox skillsEditContainer;    // contains input + tag pane
    @FXML private TextField skillInputField;
    @FXML private FlowPane skillTagPane;

    @FXML private VBox completedOpportunitiesContainer;

    // Store dynamic skill tags in edit mode
    private final List<String> skillList = new ArrayList<>();
    private final List<Rating> dummy = new ArrayList<>();
    private boolean isEditMode = false;
    private boolean isPortfolioEditMode = false;

    @FXML private HBox badgesContainer;

    public void setVolunteer(Volunteer volunteer) {
        setVolunteer(volunteer, ViewMode.VOLUNTEER_SELF_VIEW);
    }

    public void setVolunteer(Volunteer volunteer, ViewMode viewMode) {
        setUser(volunteer);
        this.currentViewMode = viewMode;

        // Set up sidebar only for volunteer self-view
        if (viewMode == ViewMode.VOLUNTEER_SELF_VIEW) {
            SidePanel sidePanel = new SidePanel(volunteer, "Profile");
            sidebarContainer.getChildren().clear();
            sidebarContainer.getChildren().add(sidePanel.getNode());
        } else {
            // Hide sidebar for organization view
            if (sidebarContainer != null) {
                sidebarContainer.setVisible(false);
                sidebarContainer.setManaged(false);
            }
        }

        // Load default content (personal info)
        if (contentContainer != null) {
            switchTabContent("/com/example/philoxapp/volunteerProfile-info.fxml");
        }

        populateFields(volunteer);

    }

    private void loadCompletedOpportunities() {
        if (completedOpportunitiesContainer == null) return;
        Volunteer v = (Volunteer) getUser();
        if (v == null) return;
        List<Opportunity> completed = ApplicationService.findCompletedByVolunteer(v.getVolunteerId());
        completedOpportunitiesContainer.getChildren().clear();

        if (completed == null || completed.isEmpty()) {
            Label placeholder = new Label("No completed opportunities yet!");
            placeholder.setWrapText(true);
            placeholder.setStyle("-fx-text-fill: #6B7280; -fx-padding: 12 16; -fx-font-size: 13px;");
            completedOpportunitiesContainer.getChildren().add(placeholder);
            return;
        }

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMM d, yyyy");
        for (Opportunity opp : completed) {
            VBox card = new VBox(6);
            card.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 10; -fx-padding: 12 16; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");

            Label title = new Label(opp.getTitle() != null ? opp.getTitle() : "");
            title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #274C56;");

            Label associatedOrgName = new Label("with " +
            (opp.getOrganisation() != null && opp.getOrganisation().getName() != null
                    ? opp.getOrganisation().getName()
                    : "Unknown Organisation"));
            associatedOrgName.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A8694;");
            associatedOrgName.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A8694;");

            String locationText = opp.getLocation() != null && !opp.getLocation().isBlank() ? opp.getLocation() : "Location not specified";
            Label locationLabel = new Label(locationText);
            locationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A8694;");

            String range = "";
            if (opp.getStartDate() != null && opp.getEndDate() != null) {
                range = opp.getStartDate().format(dateFmt) + " - " + opp.getEndDate().format(dateFmt);
            } else if (opp.getStartDate() != null) {
                range = opp.getStartDate().format(dateFmt);
            }
            StringBuilder rangeDurationBuilder = new StringBuilder();
            if (!range.isEmpty()) rangeDurationBuilder.append(range);
            if (opp.getDuration() > 0) {
                if (rangeDurationBuilder.length() > 0) rangeDurationBuilder.append(" • ");
                rangeDurationBuilder.append(opp.getDuration()).append(" hours");
            }
            Label rangeDurationLabel = new Label(rangeDurationBuilder.toString());
            rangeDurationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7A8694;");

            card.getChildren().addAll(title, associatedOrgName, locationLabel, rangeDurationLabel);
            completedOpportunitiesContainer.getChildren().add(card);
        }
    }

    private void loadVolunteerBadges(){
        if (badgesContainer == null) return;
        Volunteer v = (Volunteer) getUser();
        if (v == null) return;
        List<entity.Badge> badges = ProfileService.getVolunteerBadges(v.getVolunteerId());
        badgesContainer.getChildren().clear();

        if (badges == null || badges.isEmpty()) {
            Label placeholder = new Label("No badges earned yet!");
            placeholder.setWrapText(true);
            placeholder.setStyle("-fx-text-fill: #6B7280; -fx-padding: 12 16; -fx-font-size: 13px;");
            badgesContainer.getChildren().add(placeholder);
            return;
        }

        for (entity.Badge badge : badges) {
            VBox badgeCard = new VBox(14);
            badgeCard.setAlignment(javafx.geometry.Pos.CENTER);
            badgeCard.setPrefWidth(320);
            badgeCard.setPrefHeight(200);
            badgeCard.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0fdfa 0%, #f0fdf4 50%, #fefce8 100%);" +
                    "-fx-background-radius: 16; -fx-padding: 24;");

            ImageView badgeIcon = new ImageView();
            badgeIcon.setFitWidth(60);
            badgeIcon.setFitHeight(60);
            try {
                Image img = new Image(getClass().getResourceAsStream(badge.getIconPath()));
                badgeIcon.setImage(img);
            } catch (Exception e) {
                System.err.println("Error loading badge icon " + badge.getIconPath() + ": " + e.getMessage());
            }

            Label badgeName = new Label(badge.getBadgeName());
            badgeName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4a5565;");

            Label badgeDescription = new Label(badge.getDescription());
            badgeDescription.setWrapText(true);
            badgeDescription.setMaxWidth(280);
            badgeDescription.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            badgeDescription.setAlignment(javafx.geometry.Pos.CENTER);
            badgeDescription.setStyle("-fx-font-size: 13px; -fx-text-fill: #4a5565;");

            badgeCard.getChildren().addAll(badgeIcon, badgeName, badgeDescription);
            badgesContainer.getChildren().add(badgeCard);
        }
    }

    private void loadRatings() {

        if (ratingsContainer == null || averageRatingLabel == null) return;

        ratingsContainer.getChildren().clear();

        List<Rating> ratingList = RatingService.getLastNRatings(getUser().getUserId(), 3);

        // -------------- average rating --------------
        double avg = RatingService.getAverageRating(getUser().getUserId());
        averageRatingLabel.setText(String.format("%.1f", avg));


        // -------------- only last 3 ratings --------------
        int limit = Math.min(3, ratingList.size());
        List<Rating> recent = ratingList.subList(ratingList.size() - limit, ratingList.size());

        for (Rating r : recent) {
            RatingCard card = new RatingCard(r);
            ratingsContainer.getChildren().add(card);
        }
    }


    public void populateFields(Volunteer volunteer) {
        //set fields
        if (volunteer != null) {
            String fullName = volunteer.getName();
            String displayName = volunteer.getName();
            String email = volunteer.getEmail();
            String phone = volunteer.getPhone();
            String city = volunteer.getCity();
            String cnic = volunteer.getCnic();
            String age = String.valueOf(volunteer.getAge());

            // ----- SUMMARY BOX -----
            if (initialsLabel != null) initialsLabel.setText(generateInitials(fullName));
            if (nameLabel != null)     nameLabel.setText(displayName);
            if (emailLabel != null)    emailLabel.setText(email);
            if (phoneLabel != null)    phoneLabel.setText(phone);
            if (cityLabel != null)     cityLabel.setText(city);

            // ----- PROFILE FIELDS -----
            if (fullNameField != null) fullNameField.setText(fullName);
            if (emailField != null)    emailField.setText(email);
            if (phoneField != null)    phoneField.setText(phone);
            if (cnicField != null)     cnicField.setText(cnic);
            if (cityField != null)     cityField.setText(city);
            if (ageField != null)      ageField.setText(age);

            // ----- PORTFOLIO FIELDS -----
            if (bioLabel != null)      bioLabel.setText(volunteer.getBio() != null && !volunteer.getBio().isBlank() ? volunteer.getBio() : "e.g. Passionate about environmental conservation and community development.");

            // sync controller skillList from model on every populate
            skillList.clear();
            if (volunteer.getSkills() != null) {
                skillList.addAll(volunteer.getSkills());
            }
            if (skillsLabel != null)   skillsLabel.setText((volunteer.getSkills() != null && !volunteer.getSkills().isEmpty()) ? String.join(", ", volunteer.getSkills()) : "e.g. Team Work, Teaching, Public Speaking");

            if (fullNameField != null) disableFields();

            // Hide edit controls for organization view
            if (currentViewMode == ViewMode.ORGANIZATION_VIEW) {
                hideEditControls();
            }

            // Update header based on view mode
            updateHeaderForViewMode();
        }
        // After basic fields, if portfolio tab is active, populate completed list
        loadCompletedOpportunities();
        // and ratings
        loadRatings();
    }

    private Volunteer getTempVolunteer() {
        Volunteer tempVolunteer = new Volunteer();
        tempVolunteer.setName(fullNameField.getText());
        tempVolunteer.setEmail(emailField.getText());
        tempVolunteer.setPhone(phoneField.getText());
        tempVolunteer.setCnic(cnicField.getText());
        tempVolunteer.setCity(cityField.getText());
        tempVolunteer.setAge(Integer.parseInt(ageField.getText()));
        tempVolunteer.setBio(bioField.getText());
        tempVolunteer.setSkills(skillList);
        tempVolunteer.setVolunteerId(((Volunteer) getUser()).getVolunteerId()); // set ID for update
        System.out.println("Temp Volunteer ID: " + tempVolunteer.getVolunteerId());
        return tempVolunteer;
    }

    private boolean updateVolunteerInfoInDB() {
        Volunteer tempVolunteer = getTempVolunteer();
        boolean updateSuccess = ProfileService.updateVolunteerInfo(tempVolunteer);
        if (updateSuccess) {
            // Update current user object
            updateCurrentVolunteerInfo();
        }
        return updateSuccess;
    }

    private boolean updateVolunteerPortfolioInDB() {

        Volunteer tempVolunteer = getTempVolunteer();
        boolean updateSuccess = ProfileService.updateVolunteerPortfolio(tempVolunteer);
        if (updateSuccess) {
            // Update current user object
            updateCurrentVolunteerPortfolio();
        }
        return updateSuccess;
    }

    private void updateCurrentVolunteerInfo(){
        Volunteer currentVolunteer = (Volunteer) getUser();
        currentVolunteer.setName(fullNameField.getText());
        currentVolunteer.setEmail(emailField.getText());
        currentVolunteer.setPhone(phoneField.getText());
        currentVolunteer.setCnic(cnicField.getText());
        currentVolunteer.setCity(cityField.getText());
        currentVolunteer.setAge(Integer.parseInt(ageField.getText()));
        //update fields
        populateFields(currentVolunteer);
    }

    private void updateCurrentVolunteerPortfolio(){
        Volunteer currentVolunteer = (Volunteer) getUser();
        currentVolunteer.setBio(bioField.getText());
        currentVolunteer.setSkills(skillList);
        System.out.println("Updated volunteer portfolio skills: " + String.join(", ", skillList) + "\n");
        //update fields
        populateFields(currentVolunteer);
    }


    @FXML
    public void initialize() {

        // remove hard-coded example text overrides for skills; use populateFields to set real data or placeholders
        // keep only icon loading and basic setup

        // ----- ICONS -----
        loadIcon(fullNameIcon, "/assets/user.png");
        loadIcon(emailIcon, "/assets/email.png");
        loadIcon(formPhoneIcon, "/assets/phoneIcon.png");
        loadIcon(cnicIcon, "/assets/id-card.png");
        loadIcon(cityIcon, "/assets/locationIcon.png");
        loadIcon(ageIcon, "/assets/calendar.png");
        loadIcon(phoneIcon, "/assets/phoneIcon.png");
        loadIcon(locationIcon, "/assets/locationIcon.png");
        loadIcon(editIcon, "/assets/editIcon.png");
        loadIcon(summaryEmailIcon, "/assets/topSummaryEmail.png");
        // ----- ACHIEVEMENTS SETUP -----
        loadIcon(starBadgeImage, "/assets/badges/starBadge.png");
        loadIcon(ecoBadgeImage, "/assets/ecoWarriorBadge.png");
        loadIcon(lockBadgeImage, "/assets/lock.png");
        loadIcon(tipIcon, "/assets/tipIcon.png");
        // ----- PORTFOLIO SETUP -----
        if (portfolioEditIcon != null) {
            loadIcon(portfolioEditIcon, "/assets/editIcon.png");
        }
        // ------------------ RATINGS (dummy for now) --------------------

        //dummy.add(new Rating(5, "Amazing volunteer! Very punctual and responsible.", "Edhi Foundation", "Nov 2025"));
        //dummy.add(new Rating(4, "Great communication skills and teamwork.", "Saylani Welfare", "Oct 2025"));
        //dummy.add(new Rating(5, "Exceptional work ethic!", "Robin Hood Army", "Sep 2025"));
        //dummy.add(new Rating(3, "Good effort, can improve availability.", "Akhuwat", "Aug 2025"));

        //loadRatings(dummy);

    }

    private String generateInitials(String name) {
        String[] parts = name.trim().split(" ");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }

    private void loadIcon(ImageView view, String path) {
        if (view == null) return;
        try {
            Image img = new Image(getClass().getResourceAsStream(path));
            view.setImage(img);
        } catch (Exception e) {
            System.err.println("Error loading icon " + path + ": " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------------
    //                 PROFILE EDIT — UNTOUCHED
    // ----------------------------------------------------------------------


    private void hideEditControls() {
        // Hide main edit button
        if (editButton != null) {
            editButton.setVisible(false);
            editButton.setManaged(false);
        }

        // Hide portfolio edit button
        if (portfolioEditButton != null) {
            portfolioEditButton.setVisible(false);
            portfolioEditButton.setManaged(false);
        }
    }

    private void updateHeaderForViewMode() {
        if (currentViewMode == ViewMode.ORGANIZATION_VIEW) {
            // Update header for organization viewing volunteer profile
            if (headerTitleLabel != null) {
                headerTitleLabel.setText("Volunteer Profile");
            }
            if (headerSubtitleLabel != null) {
                headerSubtitleLabel.setText("View volunteer information and qualifications.");
            }
        } else {
            // Default header for volunteer viewing their own profile
            if (headerTitleLabel != null) {
                headerTitleLabel.setText("My Profile");
            }
            if (headerSubtitleLabel != null) {
                headerSubtitleLabel.setText("Manage your personal information and view your volunteer history.");
            }
        }
    }

    @FXML
    private void handleEditClick() {
        // Prevent editing in organization view mode
        if (currentViewMode == ViewMode.ORGANIZATION_VIEW) {
            return;
        }

        if (!isEditMode) {
            enableFields();
            isEditMode = true;

            editButton.setStyle("-fx-background-color: #009689; -fx-border-radius: 8; -fx-padding: 6 18;");
            HBox graphicBox = (HBox) editButton.getGraphic();
            Label label = (Label) graphicBox.getChildren().get(1);
            label.setText("Save");
            label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            loadIcon(editIcon, "/assets/saveIcon.png");

        } else {
            disableFields();
            isEditMode = false;

            editButton.setStyle("-fx-background-color: transparent; -fx-border-color: #949494; -fx-border-radius: 8; -fx-padding: 6 18;");
            HBox graphicBox = (HBox) editButton.getGraphic();
            Label label = (Label) graphicBox.getChildren().get(1);
            label.setText("Edit");
            label.setStyle("-fx-text-fill: #0A0A0A; -fx-font-size: 14px;");
            loadIcon(editIcon, "/assets/editIcon.png");

            // update in db
            boolean updateSuccess = updateVolunteerInfoInDB();

            if (updateSuccess) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Profile Updated");
                alert.setHeaderText(null);
                alert.setContentText("Your changes have been saved.");
                alert.showAndWait();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Update Failed");
                alert.setHeaderText(null);
                alert.setContentText("There was an error updating your profile. Please try again.");
                alert.showAndWait();
                // unupdate fields back to original
                Volunteer currentVolunteer = (Volunteer) getUser();
                fullNameField.setText(currentVolunteer.getName());
                emailField.setText(currentVolunteer.getEmail());
                phoneField.setText(currentVolunteer.getPhone());
                cnicField.setText(currentVolunteer.getCnic());
                cityField.setText(currentVolunteer.getCity());
                ageField.setText(String.valueOf(currentVolunteer.getAge()));
            }

        }
    }

    @FXML
    private void handlePortfolioEditClick() {
        // Prevent editing in organization view mode
        if (currentViewMode == ViewMode.ORGANIZATION_VIEW) {
            return;
        }

        if (portfolioEditButton == null) return;

        if (!isPortfolioEditMode) {
            enterPortfolioEditMode();
        } else {
            exitPortfolioEditMode();
        }
    }

    private void enterPortfolioEditMode() {

        isPortfolioEditMode = true;

        // Change button to save
        portfolioEditButton.setStyle("-fx-background-color: #009689; -fx-background-radius: 8;");
        loadIcon(portfolioEditIcon, "/assets/saveIcon.png");
        HBox graphicBox = (HBox) portfolioEditButton.getGraphic();
        Label label = (Label) graphicBox.getChildren().get(1);
        label.setText("Save");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: normal;");

        // Ensure skillList reflects current volunteer before building tags
        if (skillList.isEmpty() && getUser() instanceof Volunteer v && v.getSkills() != null) {
            skillList.addAll(v.getSkills());
        }

        // BIO
        bioField.setText(bioLabel.getText());
        bioField.setVisible(true);
        bioField.setManaged(true);
        bioLabel.setVisible(false);
        bioLabel.setManaged(false);

        // SKILLS
        skillsLabel.setVisible(false);
        skillsLabel.setManaged(false);

        skillsEditContainer.setManaged(true);
        skillsEditContainer.setVisible(true);

        skillTagPane.getChildren().clear();

        for (String s : skillList) {
            addSkillTag(s);
        }
    }

    private void exitPortfolioEditMode() {

        isPortfolioEditMode = false;

        // Revert button to EDIT
        portfolioEditButton.setStyle("-fx-background-color: transparent; -fx-border-color: #949494;");
        loadIcon(portfolioEditIcon, "/assets/editIcon.png");
        portfolioEditButton.setStyle("-fx-background-color: transparent; -fx-border-color: #949494; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 6 18;");
        HBox graphicBox = (HBox) portfolioEditButton.getGraphic();
        Label label = (Label) graphicBox.getChildren().get(1);
        label.setText("Edit");
        label.setStyle("-fx-text-fill: #0A0A0A; -fx-font-size: 14px; -fx-font-weight: normal;");

        // Show read-only controls
        bioLabel.setVisible(true);
        bioLabel.setManaged(true);
        bioField.setVisible(false);
        bioField.setManaged(false);

        skillsLabel.setVisible(true);
        skillsLabel.setManaged(true);
        skillsEditContainer.setVisible(false);
        skillsEditContainer.setManaged(false);

        // Update skillList from current tags
        skillList.clear();
        for (javafx.scene.Node node : skillTagPane.getChildren()) {
            if (node instanceof HBox tag) {
                Label nameLabel = (Label) tag.getChildren().get(0);
                skillList.add(nameLabel.getText());
            }
        }

        // Immediately reflect changes in the UI labels (avoid placeholder flashing)
        if (bioField.getText() != null && !bioField.getText().isBlank()) {
            bioLabel.setText(bioField.getText());
        }
        skillsLabel.setText(skillList.isEmpty() ? "e.g. Team Work, Teaching, Public Speaking" : String.join(", ", skillList));

        // Persist
        boolean updateSuccess = updateVolunteerPortfolioInDB();

        if (updateSuccess) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Portfolio Updated");
            alert.setHeaderText(null);
            alert.setContentText("Your portfolio has been updated.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Failed");
            alert.setHeaderText(null);
            alert.setContentText("There was an error updating your portfolio. Please try again.");
            alert.showAndWait();
            // revert changes from model
            Volunteer currentVolunteer = (Volunteer) getUser();
            bioLabel.setText(currentVolunteer.getBio());
            skillsLabel.setText(currentVolunteer.getSkills().isEmpty() ? "e.g. Team Work, Teaching, Public Speaking" : String.join(", ", currentVolunteer.getSkills()));
            skillList.clear();
            skillList.addAll(currentVolunteer.getSkills());
        }
    }

    @FXML
    private void handleSkillInput() {

        String input = skillInputField.getText().trim();
        if (input.isEmpty()) return;

        if (!skillList.contains(input)) {
            skillList.add(input);
            addSkillTag(input);
        }

        skillInputField.clear();
    }

    private void addSkillTag(String skill) {

        HBox tag = new HBox(6);
        tag.setStyle("-fx-background-color: #E0F2FF; -fx-padding: 2 6; -fx-background-radius: 10; -fx-border-color: #90CAF9; -fx-border-width: 1; -fx-border-radius: 10;");

        Label name = new Label(skill);
        name.setStyle("-fx-text-fill: #1565C0; -fx-font-size: 11px;");

        Button remove = new Button("✕");
        remove.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F; -fx-font-size: 12px;");
        remove.setOnAction(e -> {
            skillList.remove(skill);
            skillTagPane.getChildren().remove(tag);
        });

        tag.getChildren().addAll(name, remove);
        skillTagPane.getChildren().add(tag);
    }

    private void enableFields() {
        TextField[] fields = {fullNameField, phoneField, cnicField, cityField, ageField};

        for (TextField field : fields) {
            field.setEditable(true);
            field.setStyle("-fx-text-fill: #0A0A0A; -fx-background-color: #F9FAFB; -fx-padding: 10 12; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #ccc;");
        }

        emailField.setEditable(false);
        emailField.setStyle("-fx-text-fill: #949494; -fx-background-color: #F9FAFB; -fx-padding: 10 12; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #ccc;");
    }

    private void disableFields() {
        TextField[] fields = {fullNameField, emailField, phoneField, cnicField, cityField, ageField};
        for (TextField field : fields) {
            field.setEditable(false);
            field.setStyle("-fx-text-fill: #949494; -fx-background-color: #F9FAFB; -fx-padding: 10 12; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #ccc;");
        }
    }


    private void switchTabContent(String fxmlFile) {
        try {
            System.out.println("[DEBUG] Loading tab FXML: " + fxmlFile);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            loader.setController(this); // Use this controller for the tab content
            Parent tabContent = loader.load();

            // Replace content in the container
            contentContainer.getChildren().clear();
            contentContainer.getChildren().add(tabContent);

            //repopulate fields after loading new content (ensure after injection and add)
            Volunteer v = (Volunteer) getUser();
            System.out.println("[DEBUG] After load, nameLabel is " + (nameLabel == null ? "null" : "present") +
                    ", emailLabel is " + (emailLabel == null ? "null" : "present") +
                    ", skillsLabel is " + (skillsLabel == null ? "null" : "present"));
            populateFields(v);
            Platform.runLater(() -> {
                System.out.println("[DEBUG] Platform.runLater repopulate for: " + fxmlFile);
                populateFields(v);
                loadCompletedOpportunities();
                loadVolunteerBadges();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML private void handlePersonalInfoClick() {
        switchTabContent("/com/example/philoxapp/volunteerProfile-info.fxml");
    }

    @FXML private void handlePortfolioClick() {
        switchTabContent("/com/example/philoxapp/volunteerProfile-portfolio.fxml");
    }

    @FXML private void handleAchievementsClick() {
        switchTabContent("/com/example/philoxapp/volunteerProfile-Achievements.fxml");
    }

    /*
    private void highlightTab(Button activeTab) {
        Button[] tabs = { personalInfoTab, portfolioTab, achievementsTab };
        for (Button tab : tabs) {
            if (tab == activeTab) {
                tab.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #274C56; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 6 18;");
            } else {
                tab.setStyle("-fx-background-color: transparent; -fx-text-fill: #4A5565; -fx-background-radius: 20; -fx-padding: 6 18;");
            }
        }
     */

    private void showComingSoon(String featureName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(null);
        alert.setContentText(featureName + " screen is not implemented yet.");
        alert.showAndWait();
    }

    private void switchScene(String fxmlFile, Button sourceButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) sourceButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scene Load Error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to load " + fxmlFile + ". Please check the file path and controller.");
            alert.showAndWait();
        }
    }

}





