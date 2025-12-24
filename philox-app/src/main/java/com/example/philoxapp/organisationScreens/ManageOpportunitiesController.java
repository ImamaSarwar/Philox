package com.example.philoxapp.organisationScreens;

import com.example.philoxapp.component.sidepanel.SidePanel;
import entity.Opportunity;
import entity.Organisation;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.OpportunityStatus;
import service.ApplicationService;
import service.OpportunityService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ManageOpportunitiesController {

    @FXML private VBox sidebarContainer;


    // --- FXML Main Content ---
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<OpportunityData> opportunitiesTable;
    @FXML private TableColumn<OpportunityData, String> titleCol;
    @FXML private TableColumn<OpportunityData, String> typeCol;
    @FXML private TableColumn<OpportunityData, Label> statusCol;
    @FXML private TableColumn<OpportunityData, String> applicantsCol;
    @FXML private TableColumn<OpportunityData, String> datePostedCol;
    @FXML private TableColumn<OpportunityData, Hyperlink> viewApplicationsCol;
    @FXML private TableColumn<OpportunityData, Button> actionsCol; // Changed from MenuButton to Button
    @FXML private Label footerLabel;

    Organisation organisation;

    // --- Data Lists ---
    private ObservableList<OpportunityData> masterData = FXCollections.observableArrayList(
            opp -> new Observable[] { opp.statusProperty() }
    );
    private FilteredList<OpportunityData> filteredData;
    private SortedList<OpportunityData> sortedData;

    public void setOrganisation(Organisation org) {
        this.organisation = org;

        // Setup sidebar with new SidePanel system
        SidePanel sidePanel = new SidePanel(organisation, "Manage Opportunities");
        sidebarContainer.getChildren().clear();
        sidebarContainer.getChildren().add(sidePanel.getNode());

        loadOpportunitiesfromDatabase();

    }

    public void loadOpportunitiesfromDatabase() {

        OpportunityService.getAllOpportunitiesByOrganisation(organisation.getOrganisationId()).forEach( opp-> {
                    masterData.add(new OpportunityData(opp));
                }
        );

        // --- Setup Filtering (Search + Status) ---
        filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());

        // --- Setup Sorting ---
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(opportunitiesTable.comparatorProperty());
        opportunitiesTable.setItems(sortedData);

        updateFooterLabel();
    }

    @FXML
    public void initialize() {

        // --- Setup Filter Dropdown ---
        // Add "All Status" first, then all OpportunityStatus values
        statusFilter.getItems().add("All Status");
        for (OpportunityStatus status : OpportunityStatus.values()) {
            statusFilter.getItems().add(status.toString().toLowerCase());
        }
        statusFilter.setValue("All Status");

        // --- Setup Table ---
        setupTableColumns();

        // --- Load Mock Data ---
        //loadMockData();

        /*
        // --- Setup Filtering (Search + Status) ---
        filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());

        // --- Setup Sorting ---
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(opportunitiesTable.comparatorProperty());
        opportunitiesTable.setItems(sortedData);

        updateFooterLabel();
         */
    }

    private void updateFilter() {
        String searchText = searchField.getText().toLowerCase();
        String statusSelection = statusFilter.getValue();

        filteredData.setPredicate(opp -> {
            // 1. Check Status
            if (!"All Status".equals(statusSelection)) {
                if (!opp.getStatus().toString().equalsIgnoreCase(statusSelection)) {
                    return false;
                }
            }

            // 2. Check Search Text
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            return opp.getTitle().toLowerCase().contains(searchText) ||
                    opp.getType().toLowerCase().contains(searchText);
        });

        updateFooterLabel();
    }

    private void setupTableColumns() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Status Column
        statusCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getStatusLabel()));
        statusCol.setStyle("-fx-alignment: CENTER;");

        applicantsCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getApplicants() + " Applicants"));
        applicantsCol.setStyle("-fx-alignment: CENTER;");

        datePostedCol.setCellValueFactory(new PropertyValueFactory<>("datePosted"));
        datePostedCol.setStyle("-fx-alignment: CENTER;");

        // View Applications Column
        viewApplicationsCol.setCellValueFactory(param -> {
            OpportunityData opp = param.getValue();

            Hyperlink viewLink = new Hyperlink("View Applications");
            viewLink.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-underline: false; -fx-padding: 5; -fx-text-fill: #007bff;");
            viewLink.setOnAction(e -> handleViewApplications(opp));

            return new SimpleObjectProperty<>(viewLink);
        });
        viewApplicationsCol.setStyle("-fx-alignment: CENTER;");

        // Actions Column (Replaced MenuButton with Button + ContextMenu)
        actionsCol.setCellValueFactory(param -> {
            OpportunityData opp = param.getValue();

            // 1. Create a standard Button
            Button actionBtn = new Button("⋮");

            // 2. Style it to look transparent/clean (No CSS file needed)
            actionBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand; -fx-text-fill: #555;");

            // 3. Create the ContextMenu (Dropdown)
            ContextMenu menu = new ContextMenu();

            MenuItem closeItem = new MenuItem("Close");
            closeItem.setOnAction(e -> handleCloseAction(opp));

            MenuItem cancelItem = new MenuItem("Cancel");
            cancelItem.setStyle("-fx-text-fill: red;");
            cancelItem.setOnAction(e -> handleCancelAction(opp));

            menu.getItems().addAll(closeItem, cancelItem);

            // Disable dynamically each time before showing based on current status
            actionBtn.setOnMouseClicked(e -> {
                boolean disableBoth = opp.getStatus() == OpportunityStatus.CLOSED || opp.getStatus() == OpportunityStatus.CANCELLED;
                closeItem.setDisable(disableBoth);
                cancelItem.setDisable(disableBoth);
                menu.show(actionBtn, javafx.geometry.Side.BOTTOM, 0, 0);
            });

            return new SimpleObjectProperty<>(actionBtn);
        });
        actionsCol.setStyle("-fx-alignment: CENTER;");
    }

    private void handleCloseAction(OpportunityData opp) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close Opportunity");
        alert.setHeaderText("Are you sure you want to close '" + opp.getTitle() + "'?");
        alert.setContentText("This action is irreversible. Volunteers will no longer be able to apply.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean closeSuccess = OpportunityService.closeOpportunity(opp.opportunityId);
            if (closeSuccess) {
                opp.setStatus(OpportunityStatus.CLOSED);
                opportunitiesTable.refresh();

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText(null);
                success.setContentText("Opportunity closed successfully.");
                success.showAndWait();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText("Failed to close the opportunity. Please try again.");
                error.showAndWait();
            }
        }
    }

    private void handleCancelAction(OpportunityData opp) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Opportunity");
        alert.setHeaderText("Cancel '" + opp.getTitle() + "'?");
        alert.setContentText("This will permanently cancel the opportunity and notify associated applicants.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean cancelSuccess = OpportunityService.cancelOpportunity(opp.opportunityId);
            if (cancelSuccess) {
                opp.setStatus(OpportunityStatus.CANCELLED);
                opportunitiesTable.refresh();

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText(null);
                success.setContentText("Opportunity cancelled successfully.");
                success.showAndWait();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText("Failed to cancel the opportunity. Please try again.");
                error.showAndWait();
            }
        }
    }

    private void handleViewApplications(OpportunityData opp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/applicationsPopup.fxml"));
            Parent root = loader.load();

            ApplicationsController controller = loader.getController();
            // Set the controller to popup mode and load applications for this specific opportunity
            controller.loadApplicationsForOpportunity(opp.getOpportunityId(), opp.getTitle());

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Applications for " + opp.getTitle());
            popupStage.setResizable(true);

            Scene scene = new Scene(root);
            popupStage.setScene(scene);

            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load applications view: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void updateFooterLabel() {
        footerLabel.setText("Showing " + filteredData.size() + " of " + masterData.size() + " opportunities");
    }


    // --- Data Class ---
    public static class OpportunityData {

        private final int opportunityId;

        private final SimpleStringProperty title;
        private final SimpleStringProperty type;
        private OpportunityStatus status;
        private final SimpleIntegerProperty applicants;
        private final SimpleStringProperty datePosted;

        public OpportunityData(int opportunityId,String title, String type, int status, int applicants, String datePosted) {
            this.opportunityId = opportunityId;
            this.title = new SimpleStringProperty(title);
            this.type = new SimpleStringProperty(type);
            this.status = model.OpportunityStatus.fromCode(status);
            this.applicants = new SimpleIntegerProperty(applicants);
            this.datePosted = new SimpleStringProperty(datePosted);
        }

        public OpportunityData(Opportunity opp) {
            this.opportunityId = opp.getOpportunityId();
            this.title = new SimpleStringProperty(opp.getTitle());
            this.type = new SimpleStringProperty(opp.getCategory());
            this.status = model.OpportunityStatus.fromCode(opp.getStatus());
            this.applicants = new SimpleIntegerProperty(ApplicationService.getApplicationCountByOpportunity(opp.getOpportunityId()));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.datePosted = new SimpleStringProperty(opp.getCreatedAt().toLocalDate().format(formatter));
        }


        public int getOpportunityId() { return opportunityId; }
        public String getTitle() { return title.get(); }
        public String getType() { return type.get(); }
        public OpportunityStatus getStatus() { return status; }
        public int getApplicants() { return applicants.get(); }
        public String getDatePosted() { return datePosted.get(); }
        public void setStatus(OpportunityStatus newStatus) {
            this.status = newStatus;
        }

        public Label getStatusLabel() {
            Label lbl = new Label(getStatus().toString().substring(0, 1) + getStatus().toString().substring(1).toLowerCase());
            lbl.setPadding(new Insets(4, 12, 4, 12));
            lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 15;");

            switch (getStatus()) {
                case OPEN:
                    lbl.setStyle(lbl.getStyle() + "-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32;");
                    break;
                case CLOSED:
                    lbl.setStyle(lbl.getStyle() + "-fx-background-color: #f5f5f5; -fx-text-fill: #616161;");
                    break;
                case CANCELLED:
                    lbl.setStyle(lbl.getStyle() + "-fx-background-color: #ffebee; -fx-text-fill: #c62828;");
                    break;
                case FLAGGED:
                    lbl.setStyle(lbl.getStyle() + "-fx-background-color: #fef2f2; -fx-text-fill: #dc2626;");
                    break;
            }
            return lbl;
        }

        public Observable statusProperty() {
            return new SimpleObjectProperty<>(status);
        }
    }
}