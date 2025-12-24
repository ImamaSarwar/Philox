
package com.example.philoxapp.dashboard;

import com.example.philoxapp.component.sidepanel.SidePanel;
import entity.Organisation;
import entity.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import session.Session;

import java.util.Map;

public class OrganisationDashboardController extends UserDashboardController {

    @FXML private VBox sidebarContainer;
    @FXML private Label headerNameLabel, avatarNameLabel, welcomeLabel;
    @FXML private Circle avatarCircle;

    // Summary Cards
    @FXML private Label openOppsLabel;
    @FXML private Label toReviewLabel;
    @FXML private Label totalPostedLabel;
    @FXML private Label volunteersEngagedLabel;

    private Organisation organisation;

    @FXML
    public void initialize() {
        // Check session user safely
        User currentUser = (User) Session.getSession().getCurrentUser();
        if (currentUser instanceof Organisation) {
            this.organisation = (Organisation) currentUser;
            setupUI();
            loadDashboardData();
        } else {
            // Fallback for visual testing if no session exists
            System.out.println("No Organisation in session, running in test mode.");
        }
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
        setUser(organisation);
        setupUI();
        loadDashboardData();
    }

    private void setupUI() {
        // 1. Setup Sidebar
        SidePanel sidePanel = new SidePanel(organisation, "Dashboard");
        sidebarContainer.getChildren().setAll(sidePanel.getNode());

        // 2. Setup Header Info
        if (organisation != null) {
            String name = organisation.getName();
            if (headerNameLabel != null) headerNameLabel.setText(name);
            if (avatarNameLabel != null) avatarNameLabel.setText(name);
            if (welcomeLabel != null) welcomeLabel.setText("Welcome back, " + name + "!");
        }
        if (avatarCircle != null) avatarCircle.setFill(Color.web("#009689"));
    }

    private void loadDashboardData() {

        // load from db
        Map<String, Integer> stats = service.ProfileService.getOrganisationStats(organisation.getOrganisationId());
        if (stats != null) {
            openOppsLabel.setText(String.valueOf(stats.getOrDefault("openOpportunities", 0)));
            toReviewLabel.setText(String.valueOf(stats.getOrDefault("applicationsPendingReview", 0)));
            totalPostedLabel.setText(String.valueOf(stats.getOrDefault("totalOpportunitiesPosted", 0)));
            volunteersEngagedLabel.setText(String.valueOf(stats.getOrDefault("totalVolunteersEngaged", 0)));
            return;
        }

    }
}
