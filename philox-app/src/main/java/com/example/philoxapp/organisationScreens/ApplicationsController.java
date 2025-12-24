
package com.example.philoxapp.organisationScreens;

import com.example.philoxapp.component.modal.VolunteerProfileViewModal;
import com.example.philoxapp.component.sidepanel.SidePanel;
import entity.Application;
import entity.Organisation;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*; // Includes ComboBox
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import service.ApplicationService;
import model.ApplicationStatus;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;


public class ApplicationsController {

    @FXML private VBox sidebarContainer;

    @FXML private TextField searchField;

    // Status Filter
    @FXML private ComboBox<String> statusFilter;

    @FXML private TableView<ApplicationData> applicationsTable;
    @FXML private TableColumn<ApplicationData, VBox> volunteerNameCol;
    @FXML private TableColumn<ApplicationData, String> opportunityCol;
    @FXML private TableColumn<ApplicationData, String> appliedDateCol;
    @FXML private TableColumn<ApplicationData, Label> statusCol;
    @FXML private TableColumn<ApplicationData, HBox> actionsCol;
    @FXML private Label footerLabel;

    // Popup mode fields
    @FXML private Label opportunityTitleLabel;
    @FXML private Button closeButton;
    private boolean isPopupMode = false;

    private Organisation organisation;

    private ObservableList<ApplicationData> masterData = FXCollections.observableArrayList(
            app -> new Observable[] { app.statusProperty() }
    );
    private FilteredList<ApplicationData> filteredData;
    private SortedList<ApplicationData> sortedData;

    public void setOrganisation(Organisation org) {
        this.organisation = org;

        SidePanel sidePanel = new SidePanel(organisation, "Applications");
        sidebarContainer.getChildren().clear();
        sidebarContainer.getChildren().add(sidePanel.getNode());


        loadApplicationsFromDatabase();
    }

    @FXML
    public void initialize() {


        setupTableColumns();

        // --- UPDATED: Added "Finalised" to filter ---
        if (statusFilter != null) {
            // Add "All" first, then all ApplicationStatus values
            ObservableList<String> statusItems = FXCollections.observableArrayList();
            statusItems.add("All");
            for (ApplicationStatus status : ApplicationStatus.values()) {
                statusItems.add(status.toString().toLowerCase());
            }
            statusFilter.setItems(statusItems);
            statusFilter.setValue("All");
            statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }
    }

    private void loadApplicationsFromDatabase() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ApplicationService.getApplicationsForOrganisation(organisation.getOrganisationId()).forEach(app -> {
            String appliedDate = app.getApplicationDate() == null ? LocalDate.now().toString() : app.getApplicationDate().format(dateFormatter);
            masterData.add(new ApplicationData(app));
        });

        filteredData = new FilteredList<>(masterData, p -> true);

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }

        sortedData = new SortedList<>(filteredData);
        sortedData.setComparator(Comparator.comparing(ApplicationData::getStatus));
        applicationsTable.setItems(sortedData);

        updateFooterLabel();
    }

    private void applyFilters() {
        if (filteredData == null) return;

        String statusSelection = (statusFilter != null) ? statusFilter.getValue() : "All";
        String searchText = (searchField != null && searchField.getText() != null) ? searchField.getText().toLowerCase() : "";

        filteredData.setPredicate(app -> {
            if (statusSelection != null && !"All".equals(statusSelection)) {
                if (!app.getStatus().toString().equalsIgnoreCase(statusSelection)) {
                    return false;
                }
            }

            if (searchText.isEmpty()) return true;

            return app.getVolunteerName().toLowerCase().contains(searchText) ||
                    app.getOpportunityName().toLowerCase().contains(searchText);
        });

        updateFooterLabel();
    }

    private void setupTableColumns() {
        volunteerNameCol.setCellValueFactory(param -> {
            ApplicationData app = param.getValue();
            Label nameLabel = new Label(app.getVolunteerName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #333;");
            Label emailLabel = new Label(app.getVolunteerEmail());
            emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777;");

            VBox nameBox = new VBox(0, nameLabel, emailLabel);
            nameBox.setPadding(new Insets(5, 5, 5, 10));
            nameBox.setAlignment(Pos.CENTER_LEFT);

            return new SimpleObjectProperty<>(nameBox);
        });

        opportunityCol.setCellValueFactory(new PropertyValueFactory<>("opportunityName"));
        appliedDateCol.setCellValueFactory(new PropertyValueFactory<>("appliedDate"));
        statusCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getStatusLabel()));

        actionsCol.setCellValueFactory(param -> {
            ApplicationData app = param.getValue();
            Hyperlink viewLink = new Hyperlink("View");
            viewLink.setOnAction(e -> handleViewApplication(app));
            viewLink.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-underline: false; -fx-padding: 5; -fx-text-fill: #007bff;");
            HBox actionsBox = new HBox(viewLink);
            actionsBox.setAlignment(Pos.CENTER);
            return new SimpleObjectProperty<>(actionsBox);
        });
    }

    private void updateFooterLabel() {
        if (filteredData != null && masterData != null) {
            footerLabel.setText("Showing " + filteredData.size() + " of " + masterData.size() + " applications");
        }
    }

    private void handleViewApplication(ApplicationData app) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/philoxapp/volunteerProfileView.fxml"));
            Parent root = loader.load();

            VolunteerProfileViewModal controller = loader.getController();
            controller.setApplication(app);

            Stage modalStage = new Stage();
            modalStage.initStyle(StageStyle.TRANSPARENT);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Volunteer Profile");

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            modalStage.setScene(scene);

            modalStage.setOnHidden(e -> applicationsTable.refresh());
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load profile view: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Method to load applications for a specific opportunity (popup mode)
    public void loadApplicationsForOpportunity(int opportunityId, String opportunityTitle) {
        this.isPopupMode = true;

        // Hide sidebar in popup mode
        if (sidebarContainer != null) {
            sidebarContainer.setVisible(false);
            sidebarContainer.setManaged(false);
        }

        // Update title if in popup mode
        if (opportunityTitleLabel != null) {
            opportunityTitleLabel.setText("Applications for: " + opportunityTitle);
        }

        // Clear existing data
        masterData.clear();

        // Load applications for this specific opportunity
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ApplicationService.getApplicationsForOpportunity(opportunityId).forEach(app -> {
            masterData.add(new ApplicationData(app));
        });

        // Setup filtering
        filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        if (statusFilter != null) {
            statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }

        sortedData = new SortedList<>(filteredData);
        sortedData.setComparator(Comparator.comparing(ApplicationData::getStatus));
        applicationsTable.setItems(sortedData);

        updateFooterLabel();
    }

    @FXML
    private void handleClose() {
        if (isPopupMode && closeButton != null) {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        }
    }

    // --- Inner Data Class ---
    public static class ApplicationData {
        private final Application application;
        private final SimpleStringProperty volunteerName;
        private final SimpleStringProperty volunteerEmail;
        private final SimpleStringProperty volunteerCity;
        private final SimpleStringProperty opportunityName;
        private final SimpleStringProperty appliedDate;
        private final SimpleObjectProperty<ApplicationStatus> status;
        private final SimpleStringProperty skills;
        private final SimpleStringProperty applicationMessage;

        public ApplicationData(Application application) {
            this.application = application;
            this.volunteerName = new SimpleStringProperty(application.getVolunteer().getName());
            this.volunteerEmail = new SimpleStringProperty(application.getVolunteer().getEmail());
            this.opportunityName = new SimpleStringProperty(application.getOpportunity().getTitle());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String appliedDateStr = application.getApplicationDate() == null ? LocalDate.now().toString() : application.getApplicationDate().format(dateFormatter);
            this.appliedDate = new SimpleStringProperty(appliedDateStr);
            this.skills = new SimpleStringProperty(application.getVolunteer().getSkills() != null ? String.join(", ", application.getVolunteer().getSkills()) : "");
            this.status = new SimpleObjectProperty<>(ApplicationStatus.fromCode(application.getStatus()));

            this.volunteerCity = new SimpleStringProperty(application.getVolunteer().getCity());
            this.applicationMessage = new SimpleStringProperty(application.getApplicationComment());
        }

        public Application getApplication(){ return this.application; }
        public String getVolunteerName() { return volunteerName.get(); }
        public String getVolunteerEmail() { return volunteerEmail.get(); }
        public String getOpportunityName() { return opportunityName.get(); }
        public String getAppliedDate() { return appliedDate.get(); }
        public ApplicationStatus getStatus() { return status.get(); }
        public String getVolunteerCity() { return volunteerCity.get(); }
        public String getSkills() { return skills.get(); }
        public String getApplicationMessage() { return applicationMessage.get(); }

        public void setStatus(ApplicationStatus newStatus) { this.status.set(newStatus); }
        public SimpleObjectProperty<ApplicationStatus> statusProperty() { return status; }

        public Label getStatusLabel() {
            Label statusLabel = new Label(this.status.get().toString());
            statusLabel.setPadding(new Insets(5, 10, 5, 10));

            switch (this.status.get()) {
                case PENDING:
                    statusLabel.setStyle("-fx-background-color: #fff8e1; -fx-text-fill: #f57f17; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 6;");
                    break;
                case ACCEPTED:
                    statusLabel.setStyle("-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 6;");
                    break;
                case REJECTED:
                    statusLabel.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 6;");
                    break;
                case FINALISED:
                    statusLabel.setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 6;");
                    break;
                case WITHDRAWN:
                    statusLabel.setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 6;");
                    break;
            }
            return statusLabel;
        }
    }
}
