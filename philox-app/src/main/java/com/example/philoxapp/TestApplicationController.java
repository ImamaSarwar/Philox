package com.example.philoxapp;

import entity.Volunteer;
import entity.Opportunity;
import entity.Application;
import service.OpportunityService;
import service.ApplicationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TestApplicationController {

    private Volunteer volunteer;

    @FXML
    private TableView<Opportunity> opportunityTable;
    @FXML
    private TableColumn<Opportunity, String> titleColumn;
    @FXML
    private TableColumn<Opportunity, String> categoryColumn;
    @FXML
    private TextArea commentArea;
    @FXML
    private Button applyButton;

    private ObservableList<Opportunity> opportunities = FXCollections.observableArrayList();

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    @FXML
    public void initialize() {
        // Setup table columns
        titleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));

        // Load opportunities
        opportunities.setAll(OpportunityService.getAllOpenOpportunities());
        opportunityTable.setItems(opportunities);

        applyButton.setOnAction(e -> handleApply());
    }

    private void handleApply() {
        Opportunity selected = opportunityTable.getSelectionModel().getSelectedItem();
        if (selected == null || volunteer == null) {
            showAlert("Please select an opportunity and ensure volunteer is set.");
            return;
        }
        String comment = commentArea.getText();
        Application app = new Application();
        app.setOpportunityId(selected.getOpportunityId());
        app.setVolunteerId(volunteer.getVolunteerId());
        app.setApplicationComment(comment);
        app.setStatus(0);

        boolean success = ApplicationService.applyForOpportunity(app);
        if (success) {
            showAlert("Application submitted!");
        } else {
            showAlert("Failed to submit application.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
