package com.example.philoxapp.adminScreens;

import com.example.philoxapp.popUps.AdminOrgDetailsPopupController;
import com.example.philoxapp.component.sidepanel.SidePanel;
import db.repository.OrganisationRepository;
import entity.Admin;
import entity.Organisation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminOrganisationsController {

    @FXML private VBox sidebarContainer;
    @FXML private TilePane cardsContainer;
    @FXML private ScrollPane mainScrollPane;

    @FXML private Button filterPending, filterApproved, filterRejected, filterAll;

    private Admin admin;
    private List<Organisation> allOrganisations = new ArrayList<>();
    private int currentFilter = 0; // 0=Pending

    public void setAdmin(Admin admin) {
        this.admin = admin;
        SidePanel sidePanel = new SidePanel(admin, "Organisations");
        sidebarContainer.getChildren().setAll(sidePanel.getNode());

        if (mainScrollPane != null) {
            cardsContainer.prefTileWidthProperty().bind(
                    mainScrollPane.widthProperty().subtract(100).divide(2)
            );
        }

        loadData();
    }

    public void loadData() {
        allOrganisations = OrganisationRepository.getAllOrganisations();
        /*
        if (allOrganisations.isEmpty()) {
            generateMockData();
        }
         */
        updateFilterCounts();
        refreshView();
    }

    private void updateFilterCounts() {
        long pending = allOrganisations.stream().filter(o -> o.getStatus() == 0).count();
        long approved = allOrganisations.stream().filter(o -> o.getStatus() == 1).count();
        long rejected = allOrganisations.stream().filter(o -> o.getStatus() == -1).count();
        long all = allOrganisations.size();

        filterPending.setText("Pending (" + pending + ")");
        filterApproved.setText("Approved (" + approved + ")");
        filterRejected.setText("Rejected (" + rejected + ")");
        filterAll.setText("All (" + all + ")");
    }

    private void generateMockData() {
        Organisation o1 = new Organisation(); o1.setOrganisationId(101); o1.setName("Green Earth Foundation"); o1.setEmail("contact@greenearth.org"); o1.setStatus(0); o1.setMission("Dedicated to environmental conservation.");
        Organisation o2 = new Organisation(); o2.setOrganisationId(102); o2.setName("Youth Empowerment Hub"); o2.setEmail("info@youthempowerment.org"); o2.setStatus(0); o2.setMission("Empowering young people through skill development.");
        Organisation o3 = new Organisation(); o3.setOrganisationId(103); o3.setName("Community Health Initiative"); o3.setEmail("admin@communityhealth.org"); o3.setStatus(1); o3.setMission("Providing healthcare access.");
        Organisation o4 = new Organisation(); o4.setOrganisationId(104); o4.setName("Education for All"); o4.setEmail("hello@educationforall.org"); o4.setStatus(-1); o4.setMission("Breaking barriers to education.");
        allOrganisations.add(o1);
        allOrganisations.add(o2);
        allOrganisations.add(o3);
        allOrganisations.add(o4);
    }

    public void refreshData() { loadData(); }

    private void refreshView() {
        resetButtonStyle(filterPending);
        resetButtonStyle(filterApproved);
        resetButtonStyle(filterRejected);
        resetButtonStyle(filterAll);

        Button activeBtn = filterAll;
        if (currentFilter == 0) activeBtn = filterPending;
        else if (currentFilter == 1) activeBtn = filterApproved;
        else if (currentFilter == -1) activeBtn = filterRejected;

        activeBtn.setStyle("-fx-background-color: #1F2937; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 8 20; -fx-cursor: hand;");

        List<Organisation> filtered = allOrganisations.stream()
                .filter(o -> currentFilter == 99 || o.getStatus() == currentFilter)
                .collect(Collectors.toList());

        renderCards(filtered);
    }

    @FXML private void filterPending() { currentFilter = 0; refreshView(); }
    @FXML private void filterApproved() { currentFilter = 1; refreshView(); }
    @FXML private void filterRejected() { currentFilter = -1; refreshView(); }
    @FXML private void filterAll() { currentFilter = 99; refreshView(); }

    private void resetButtonStyle(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #4B5563; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 8 20; -fx-cursor: hand;");
    }

    private void renderCards(List<Organisation> orgs) {
        cardsContainer.getChildren().clear();
        for (Organisation org : orgs) {
            cardsContainer.getChildren().add(createCard(org));
        }
    }

    private VBox createCard(Organisation org) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0.1, 0, 2);");

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/assets/orgLabelIcon.png")));
        icon.setFitHeight(40); icon.setFitWidth(40);

        VBox info = new VBox(0);
        Label name = new Label(org.getName());
        // FIX: Changed font-weight to normal
        name.setStyle("-fx-font-weight: normal; -fx-font-size: 16px; -fx-text-fill: #333;");
        Label email = new Label(org.getEmail());
        email.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        info.getChildren().addAll(name, email);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = new Label();
        statusBadge.setStyle("-fx-padding: 5 12; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 11px;");
        if (org.getStatus() == 0) {
            statusBadge.setText("Pending");
            statusBadge.setStyle(statusBadge.getStyle() + "-fx-background-color: #FFFBEB; -fx-text-fill: #B45309;");
        } else if (org.getStatus() == 1) {
            statusBadge.setText("Approved");
            statusBadge.setStyle(statusBadge.getStyle() + "-fx-background-color: #E6FFFA; -fx-text-fill: #047857;");
        } else {
            statusBadge.setText("Rejected");
            statusBadge.setStyle(statusBadge.getStyle() + "-fx-background-color: #FEF2F2; -fx-text-fill: #B91C1C;");
        }

        header.getChildren().addAll(icon, info, spacer, statusBadge);

        Label desc = new Label(org.getMission() != null ? org.getMission() : "No mission provided.");
        desc.setWrapText(true);
        desc.setPrefHeight(40);
        desc.setStyle("-fx-text-fill: #555; -fx-font-size: 13px;");

        HBox meta = new HBox(15);
        Label date = new Label("Submitted: " + (org.getRegistrationDate() != null ? org.getRegistrationDate() : "N/A"));
        date.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");
        meta.getChildren().add(date);

        HBox docs = new HBox(8);
        String[] docTypes = {"Registration", "Tax", "Statement"};
        for (String dt : docTypes) {
            Label d = new Label(dt);
            d.setStyle("-fx-background-color: #F3F4F6; -fx-text-fill: #374151; -fx-padding: 4 8; -fx-background-radius: 6; -fx-font-size: 10px;");
            docs.getChildren().add(d);
        }

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);

        Button viewBtn = new Button("View Details");
        viewBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(viewBtn, Priority.ALWAYS);
        viewBtn.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-text-fill: #333; -fx-background-radius: 6; -fx-border-radius: 6; -fx-cursor: hand;");
        viewBtn.setOnAction(e -> openDetailsPopup(org));
        actions.getChildren().add(viewBtn);

        card.getChildren().addAll(header, desc, meta, docs, actions);
        return card;
    }

    private void handleUpdateStatus(Organisation org, int status) {
        boolean success = OrganisationRepository.updateStatus(org.getOrganisationId(), status);
        // Mock success
        success = true;
        if (success) {
            org.setStatus(status);
            updateFilterCounts();
            refreshView();
        }
    }

    private void openDetailsPopup(Organisation org) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/adminOrgDetailsPopup.fxml"));
            Parent root = loader.load();

            AdminOrgDetailsPopupController controller = loader.getController();
            controller.setOrganisation(org, this);

            Stage popup = new Stage(StageStyle.TRANSPARENT);
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setScene(new Scene(root, Color.TRANSPARENT));
            popup.initOwner(sidebarContainer.getScene().getWindow());
            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}