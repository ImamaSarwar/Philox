package com.example.philoxapp.component.sidepanel;

import entity.Admin;
import entity.Organisation;
import entity.Volunteer;
import entity.User;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import service.auth.AuthService;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Self-contained SidePanel that creates its own UI and handles navigation based on user role
 */
public class SidePanel {

    private final User user;
    private String currentPage; // made mutable for refresh
    private List<NavItem> navItems;
    private VBox sidePanelNode;

    public SidePanel(User user, String currentPage) {
        this.user = user;
        this.currentPage = currentPage;
        loadNavigationItems();
        createSidePanel();
    }

    /**
     * Load navigation items based on user role
     */
    private void loadNavigationItems() {
        if (user instanceof Organisation) {
            navItems = createOrganisationNavItems();
        }
        else if (user instanceof Volunteer) {
            navItems = createVolunteerNavItems();
        }
        else if (user instanceof Admin){
            navItems = createAdminNavItems();
        }
        else {
            throw new IllegalArgumentException("Unsupported user type: " + user.getClass().getSimpleName());
        }
    }

    /**
     * Create navigation items for Organisation users
     */
    private List<NavItem> createOrganisationNavItems() {
        System.out.println("Creating navigation items for Organisation");
        return Arrays.asList(
            new NavItem("Dashboard", "/assets/dashboardIcon.png", "com.example.philoxapp.dashboard.OrganisationDashboardController"),
            new NavItem("Post Opportunity", "/assets/icons/edit_icon.png", "com.example.philoxapp.organisationScreens.PostOpportunityController"),
            new NavItem("Manage Opportunities", "/assets/building.png", "com.example.philoxapp.organisationScreens.ManageOpportunitiesController"),
            new NavItem("Applications", "/assets/applicationsIcon.png", "com.example.philoxapp.organisationScreens.ApplicationsController"),
            new NavItem("Profile", "/assets/profile-green.png", "com.example.philoxapp.profile.OrganisationProfileController"),
            new NavItem("Settings", "/assets/settingsIcon.png", "com.example.philoxapp.settings.SettingsController")
        );
    }

    private List<NavItem> createVolunteerNavItems() {
        return Arrays.asList(
                new NavItem("Dashboard", "/assets/dashboardIcon.png", "com.example.philoxapp.volunteer.VolunteerDashboardController"),
                new NavItem("Opportunities", "/assets/opportunitiesIcon.png", "com.example.philoxapp.volunteer.OpportunitiesController"),
                new NavItem("My Applications", "/assets/applicationsIcon.png", "com.example.philoxapp.volunteer.VolunteerApplicationTrackingController"),
                new NavItem("Profile", "/assets/profile-green.png", "com.example.philoxapp.volunteer.VolunteerProfileController"),
                new NavItem("Settings", "/assets/settingsIcon.png", "com.example.philoxapp.settings.SettingsController")
        );
    }

    private List<NavItem> createAdminNavItems() {
        return Arrays.asList(
                new NavItem("Dashboard", "/assets/dashboardIcon.png", "com.example.philoxapp.admin.AdminDashboardController"),
                new NavItem("Organisations", "/assets/building.png", "com.example.philoxapp.admin.AdminOrganisationsController"),
                new NavItem("Posts", "/assets/icons/post_icon.png", "com.example.philoxapp.admin.AdminPostsController"),
                new NavItem("Badges", "/assets/icons/badge_icon.png", "com.example.philoxapp.admin.BadgesController"),
                new NavItem("Database Panel", "/assets/icons/database_icon.png", "com.example.philoxapp.DatabasePanelController")

        );
    }

    /**
     * Create the side panel UI programmatically
     */
    private void createSidePanel() {
        sidePanelNode = new VBox();
        sidePanelNode.setSpacing(20);
        sidePanelNode.setAlignment(Pos.TOP_LEFT);
        sidePanelNode.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-pref-width: 250;");
        VBox logoSection = createLogoSection();
        sidePanelNode.getChildren().add(logoSection);
        VBox navigationSection = createNavigationSection();
        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        VBox userSection = createUserSection();
        sidePanelNode.getChildren().addAll(navigationSection, spacer, userSection);
    }

    /**
     * Create the logo section
     */
    private VBox createLogoSection() {
        VBox logoSection = new VBox();
        logoSection.setSpacing(5);
        logoSection.setAlignment(Pos.TOP_LEFT);

        HBox logoBox = new HBox();
        logoBox.setSpacing(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        try {
            ImageView logoImage = new ImageView(new Image(getClass().getResourceAsStream("/assets/Philox-Logo.png")));
            logoImage.setFitHeight(30);
            logoImage.setFitWidth(30);
            logoBox.getChildren().add(logoImage);
        } catch (Exception e) {
            System.err.println("Logo image not found");
        }

        VBox titleBox = new VBox();
        Label titleLabel = new Label("Philox");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #274C56;");
        Label subtitleLabel = new Label("Portal");
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);

        logoBox.getChildren().add(titleBox);

        VBox separatorBox = new VBox();
        separatorBox.setStyle("-fx-padding: 10 0 0 0;");
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setMaxWidth(200);
        separator.setStyle("-fx-background-color: #DEDEDE;");
        separatorBox.getChildren().add(separator);

        logoSection.getChildren().addAll(logoBox, separatorBox);
        return logoSection;
    }

    /**
     * Create the navigation section with dynamic links
     */
    private VBox createNavigationSection() {
        VBox navigationSection = new VBox();
        navigationSection.setSpacing(10);
        navigationSection.setStyle("-fx-padding: 10 0;");

        for (NavItem navItem : navItems) {
            HBox linkBox = createNavigationLink(navItem);
            navigationSection.getChildren().add(linkBox);
        }

        return navigationSection;
    }

    /**
     * Create a single navigation link
     */
    private HBox createNavigationLink(NavItem navItem) {
        HBox linkBox = new HBox();
        linkBox.setSpacing(10);
        linkBox.setAlignment(Pos.CENTER_LEFT);

        boolean isSelected = navItem.getTitle().equals(currentPage);

        if (isSelected) {
            linkBox.setStyle("-fx-background-color: #F0FDFA; -fx-padding: 10 15; -fx-background-radius: 8; -fx-cursor: hand;");
        } else {
            linkBox.setStyle("-fx-background-color: transparent; -fx-padding: 10 15; -fx-background-radius: 8; -fx-cursor: hand;");
        }

        // Add hover effects
        linkBox.setOnMouseEntered(e -> {
            if (!isSelected) {
                linkBox.setStyle("-fx-background-color: #F5F5F5; -fx-padding: 10 15; -fx-background-radius: 8; -fx-cursor: hand;");
            }
        });

        linkBox.setOnMouseExited(e -> {
            if (!isSelected) {
                linkBox.setStyle("-fx-background-color: transparent; -fx-padding: 10 15; -fx-background-radius: 8; -fx-cursor: hand;");
            }
        });

        // Add icon
        if (navItem.getIcon() != null && !navItem.getIcon().isEmpty()) {
            try {
                ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(navItem.getIcon())));
                icon.setFitHeight(20);
                icon.setFitWidth(20);
                linkBox.getChildren().add(icon);
            } catch (Exception e) {
                System.err.println("Icon not found: " + navItem.getIcon());
                // Add placeholder
                ImageView placeholder = new ImageView();
                placeholder.setFitHeight(20);
                placeholder.setFitWidth(20);
                linkBox.getChildren().add(placeholder);
            }
        }

        // Add text label
        Label textLabel = new Label(navItem.getTitle());
        if (isSelected) {
            textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #00786F; -fx-font-weight: bold;");
        } else {
            textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #274C56;");
        }
        linkBox.getChildren().add(textLabel);

        // Set click handler
        linkBox.setOnMouseClicked(e -> handleNavigation(navItem));

        return linkBox;
    }

    /**
     * Create the user info section
     */
    private VBox createUserSection() {
        VBox userSection = new VBox();
        userSection.setSpacing(10);

        // Separator
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setMaxWidth(200);
        separator.setStyle("-fx-background-color: #DEDEDE;");

        // User info
        HBox userInfoBox = new HBox();
        userInfoBox.setSpacing(10);
        userInfoBox.setAlignment(Pos.CENTER_LEFT);

        // User avatar
        StackPane avatarPane = new StackPane();
        avatarPane.setPrefWidth(36);
        avatarPane.setPrefHeight(36);
        avatarPane.setStyle("-fx-background-color: #009689; -fx-background-radius: 18;");

        Label initialsLabel = new Label(generateInitials(user.getName()));
        initialsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        avatarPane.getChildren().add(initialsLabel);

        // User details
        VBox userDetailsBox = new VBox();
        Label nameLabel = new Label(user.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #274C56;");
        Label roleLabel = new Label(
                user instanceof Organisation ? "Organisation" :
                user instanceof Volunteer ? "Volunteer" :
                user instanceof Admin ? "Admin" : "User"
        );
        roleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        userDetailsBox.getChildren().addAll(nameLabel, roleLabel);

        userInfoBox.getChildren().addAll(avatarPane, userDetailsBox);

        // Logout
        HBox logoutBox = new HBox();
        logoutBox.setSpacing(10);
        logoutBox.setAlignment(Pos.CENTER_LEFT);
        logoutBox.setStyle("-fx-background-color: transparent; -fx-padding: 8 0; -fx-cursor: hand;");
        logoutBox.setOnMouseClicked(e -> handleLogout());

        try {
            ImageView logoutIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/logout.png")));
            logoutIcon.setFitHeight(18);
            logoutIcon.setFitWidth(18);
            logoutBox.getChildren().add(logoutIcon);
        } catch (Exception e) {
            System.err.println("Logout icon not found");
        }

        Label logoutLabel = new Label("Logout");
        logoutLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4A5565;");
        logoutBox.getChildren().add(logoutLabel);

        userSection.getChildren().addAll(separator, userInfoBox, logoutBox);
        return userSection;
    }

    /**
     * Generate initials from user name
     */
    private String generateInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "U";
        String[] parts = name.trim().split(" ");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length-1].substring(0, 1)).toUpperCase();
    }

    /**
     * Handle navigation to the specified controller
     */
    private void handleNavigation(NavItem navItem) {
        try {
            String fxmlPath = getFxmlPathFromController(navItem.getController());

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Get the controller and set user data if applicable
            Object controller = loader.getController();
            setUserInController(controller);

            // Navigate to the new scene
            Scene scene = new Scene(root);
            Stage stage = getCurrentStage();

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());

            stage.setScene(scene);
            stage.setTitle("Philox - " + navItem.getTitle());

            refreshSelection(navItem.getTitle());

        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to navigate to " + navItem.getTitle() + ": " + e.getMessage());
        }
    }

    /**
     * Convert controller class name to FXML file path
     */
    private String getFxmlPathFromController(String controllerClass) {
        // Map controller classes to FXML files
        switch (controllerClass) {
            // organisation controllers
            case "com.example.philoxapp.dashboard.OrganisationDashboardController":
                return "/com/example/philoxapp/organisationDashboard.fxml";
            case "com.example.philoxapp.organisationScreens.PostOpportunityController":
                return "/com/example/philoxapp/postOpportunity.fxml";
            case "com.example.philoxapp.organisationScreens.ManageOpportunitiesController":
                return "/com/example/philoxapp/manageOpportunities.fxml";
            case "com.example.philoxapp.organisationScreens.ApplicationsController":
                return "/com/example/philoxapp/applications.fxml";
            case "com.example.philoxapp.profile.OrganisationProfileController":
                return "/com/example/philoxapp/organisationProfile.fxml";
            case "com.example.philoxapp.settings.SettingsController":
                return "/com/example/philoxapp/userSettings.fxml";
            //  volunteer controllers
            case "com.example.philoxapp.volunteer.VolunteerDashboardController":
                return "/com/example/philoxapp/volunteerDashboard.fxml";
            case "com.example.philoxapp.volunteer.OpportunitiesController":
                return "/com/example/philoxapp/volunteerOpportunities.fxml";
            case "com.example.philoxapp.volunteer.VolunteerApplicationTrackingController":
                return "/com/example/philoxapp/VolunteerApplicationTracking.fxml";
            case "com.example.philoxapp.volunteer.VolunteerProfileController":
                return "/com/example/philoxapp/volunteerProfile.fxml";
            case "com.example.philoxapp.volunteer.VolunteerSettingsController":
                return "/com/example/philoxapp/userSettings.fxml";
            /*case "com.example.philoxapp.settings.SettingsController":
                return "/com/example/philoxapp/userSettings.fxml";*/
            // admin controllers
            case "com.example.philoxapp.admin.AdminDashboardController":
                return "/com/example/philoxapp/AdminDashboard.fxml";
            case "com.example.philoxapp.admin.AdminOrganisationsController":
                return "/com/example/philoxapp/adminOrganisations.fxml";
            case "com.example.philoxapp.admin.AdminPostsController":
                return "/com/example/philoxapp/adminPosts.fxml";
            case "com.example.philoxapp.admin.BadgesController":
                return "/com/example/philoxapp/adminBadges.fxml";
            case "com.example.philoxapp.DatabasePanelController":
                return "/com/example/philoxapp/databasePanel.fxml";
            default:
                throw new IllegalArgumentException("Unknown controller: " + controllerClass);
        }
    }

    /**
     * Set user data in the target controller if it has the appropriate setter method
     */
    private void setUserInController(Object controller) {
        try {
            if (user instanceof Organisation) {
                // Try to find setOrganisation method
                Method setOrgMethod = controller.getClass().getMethod("setOrganisation", Organisation.class);
                setOrgMethod.invoke(controller, (Organisation) user);
            }
            else if (user instanceof Volunteer) {
                // Try to find setVolunteer method
                Method setVolMethod = controller.getClass().getMethod("setVolunteer", Volunteer.class);
                setVolMethod.invoke(controller, (Volunteer) user);
            }
            else if (user instanceof Admin) {
                // Try to find setAdmin method
                Method setAdminMethod = controller.getClass().getMethod("setAdmin", Admin.class);
                setAdminMethod.invoke(controller, (Admin) user);
            }
            else {
                // Try to find setUser method
                Method setUserMethod = controller.getClass().getMethod("setUser", User.class);
                setUserMethod.invoke(controller, user);
            }

        } catch (Exception e) {
            // Controller doesn't have the setter method, that's okay
            System.out.println("Controller " + controller.getClass().getSimpleName() + " doesn't have user setter method: " + e.getMessage());
        }
    }

    /**
     * Handle logout functionality
     */
    public void handleLogout() {
        Stage currentStage = getCurrentStage();
        AuthService.logout(currentStage);
    }

    /**
     * Get the side panel node to add to your layout
     */
    public Node getNode() {
        return sidePanelNode;
    }

    /**
     * Get the current user
     */
    public User getUser() {
        return user;
    }

    /**
     * Get the current page name
     */
    public String getCurrentPage() {
        return currentPage;
    }

    /**
     * Get the navigation items
     */
    public List<NavItem> getNavItems() {
        return navItems;
    }

    public void refreshSelection(String newPage) {
        this.currentPage = newPage;
        // find navigation section (first VBox whose children are HBox links)
        for (Node child : sidePanelNode.getChildren()) {
            if (child instanceof VBox) {
                VBox vb = (VBox) child;
                boolean looksLikeNav = vb.getChildren().stream().allMatch(n -> n instanceof HBox);
                if (looksLikeNav) {
                    vb.getChildren().clear();
                    for (NavItem navItem : navItems) {
                        vb.getChildren().add(createNavigationLink(navItem));
                    }
                    break;
                }
            }
        }
    }

    // --- Helper Methods ---

    private Stage getCurrentStage() {
        return (Stage) sidePanelNode.getScene().getWindow();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
