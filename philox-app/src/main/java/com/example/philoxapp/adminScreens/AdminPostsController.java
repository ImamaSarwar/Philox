package com.example.philoxapp.adminScreens;

import com.example.philoxapp.component.sidepanel.SidePanel;
import entity.Admin;
import entity.Opportunity;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import service.AdministrativeService;
import service.OpportunityService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AdminPostsController {

    @FXML private VBox sidebarContainer;
    @FXML private TilePane cardsContainer;
    @FXML private ScrollPane mainScrollPane;

    private Admin admin;
    private List<Opportunity> posts;

    public void setAdmin(Admin admin) {
        this.admin = admin;
        SidePanel sidePanel = new SidePanel(admin, "Posts");
        sidebarContainer.getChildren().setAll(sidePanel.getNode());

        if (mainScrollPane != null) {
            cardsContainer.prefTileWidthProperty().bind(
                    mainScrollPane.widthProperty().subtract(100).divide(3)
            );
        }

        loadData();
    }

    private void loadData() {
        try {
            posts = OpportunityService.getAllOpportunities();
        } catch (Exception e) {
            posts = new ArrayList<>();
        }

        /*
        if (posts == null || posts.isEmpty()) {
            posts = new ArrayList<>();
            Organisation mockOrg = new Organisation(); mockOrg.setName("Mock Org");
            Opportunity mockOpp = new Opportunity(); mockOpp.setTitle("Mock Opportunity"); mockOpp.setOrganisation(mockOrg);
            mockOpp.setCategory("Education"); mockOpp.setDescription("Description..."); mockOpp.setLocation("City");
            posts.add(mockOpp);
        }
         */

        renderCards();
    }

    private void renderCards() {
        cardsContainer.getChildren().clear();
        if (posts.isEmpty()) {
            Label empty = new Label("No posts to display.");
            empty.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
            cardsContainer.getChildren().add(empty);
            return;
        }
        for (Opportunity opp : posts) {
            cardsContainer.getChildren().add(createCard(opp));
        }
    }

    private VBox createCard(Opportunity opp) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0.1, 0, 2);");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label(opp.getTitle());
        title.setWrapText(true);
        title.setMinHeight(Region.USE_PREF_SIZE);

        // FIX: Removed bold, made font normal weight
        title.setStyle("-fx-font-weight: normal; -fx-font-size: 16px; -fx-text-fill: #333;");

        String orgName = (opp.getOrganisation() != null) ? opp.getOrganisation().getName() : "Unknown Org";
        Label orgLabel = new Label("by " + orgName);
        orgLabel.setStyle("-fx-text-fill: #009689; -fx-font-weight: bold; -fx-font-size: 12px;");

        titleBox.getChildren().addAll(title, orgLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label catBadge = new Label(opp.getCategory() != null ? opp.getCategory() : "General");
        catBadge.setStyle("-fx-background-color: #F3F4F6; -fx-text-fill: #4B5563; -fx-padding: 4 8; -fx-background-radius: 6; -fx-font-size: 10px;");

        header.getChildren().addAll(titleBox, spacer, catBadge);

        Label desc = new Label(opp.getDescription());
        desc.setWrapText(true);
        desc.setPrefHeight(50);
        desc.setStyle("-fx-text-fill: #555; -fx-font-size: 13px;");

        HBox meta = new HBox(15);
        meta.setAlignment(Pos.CENTER_LEFT);

        HBox locBox = new HBox(5);
        locBox.setAlignment(Pos.CENTER_LEFT);
        ImageView locIcon = new ImageView();
        locIcon.setFitHeight(14); locIcon.setFitWidth(14);
        loadIconIntoView(locIcon, "/assets/locationIcon.png");

        Label locLabel = new Label(opp.getLocation());
        locLabel.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");
        locBox.getChildren().addAll(locIcon, locLabel);

        Label dateLabel = new Label("📅 " + (opp.getStartDate() != null ? opp.getStartDate().toString() : "TBD"));
        dateLabel.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");

        meta.getChildren().addAll(locBox, dateLabel);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #f0f0f0; -fx-opacity: 0.5;");

        HBox actions = new HBox();
        actions.setAlignment(Pos.CENTER_RIGHT);
        Button flagBtn = new Button("Flag Post");
        flagBtn.setStyle("-fx-background-color: white; -fx-border-color: #DC2626; -fx-text-fill: #DC2626; -fx-border-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;");
        flagBtn.setOnAction(e -> handleFlag(card, opp));
        actions.getChildren().add(flagBtn);

        card.getChildren().addAll(header, desc, meta, sep, actions);
        return card;
    }

    private void handleFlag(VBox card, Opportunity opp) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Flag '" + opp.getTitle() + "'?", ButtonType.YES, ButtonType.NO);
        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            if (AdministrativeService.flagOpportunityPost(opp.getOpportunityId())){
                Alert info = new Alert(Alert.AlertType.INFORMATION, "Post flagged successfully.", ButtonType.OK);
                info.showAndWait();
                cardsContainer.getChildren().remove(card);
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR, "Failed to flag the post.", ButtonType.OK);
                error.showAndWait();
            }
        }
    }

    private void loadIconIntoView(ImageView view, String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) view.setImage(new Image(is));
        } catch (Exception e) { }
    }
}