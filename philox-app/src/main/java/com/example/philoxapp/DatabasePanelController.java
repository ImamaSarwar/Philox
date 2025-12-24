package com.example.philoxapp;

import com.example.philoxapp.component.sidepanel.SidePanel;
import db.DBConnection;
import entity.Admin;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DatabasePanelController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private VBox sidebarContainer;
    @FXML private ComboBox<String> tableSelector;
    @FXML private Button loadTableButton;
    @FXML private Button refreshButton;
    @FXML private Label recordCountLabel;
    @FXML private Label tableNameLabel;
    @FXML private Label statusLabel;
    @FXML private Label lastUpdatedLabel;
    @FXML private TextArea dataTextArea;

    private Admin admin;

    // Available database tables
    private final List<String> availableTables = Arrays.asList(
        "USERS", "VOLUNTEER", "ORGANISATION", "ORGANISATION_LICENSE", "ORGANISATION_SOCIAL_MEDIA",
        "OPPORTUNITY", "OPPORTUNITY_CATEGORY", "APPLICATION", "RATING", "BADGE",
        "VOLUNTEER_BADGE", "ADMIN"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableSelector();
        setupDataTextArea();
        updateStatus("Ready to load table data");
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        setupSidebar();
    }

    private void setupSidebar() {
        if (admin != null && sidebarContainer != null) {
            SidePanel sidePanel = new SidePanel(admin, "Database Panel");
            sidebarContainer.getChildren().clear();
            sidebarContainer.getChildren().add(sidePanel.getNode());
        }
    }

    private void setupTableSelector() {
        if (tableSelector != null) {
            tableSelector.setItems(FXCollections.observableArrayList(availableTables));
            tableSelector.setPromptText("Choose a table...");

            // Enable load button only when a table is selected
            tableSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (loadTableButton != null) {
                    loadTableButton.setDisable(newVal == null || newVal.isEmpty());
                }
            });
        }
    }

    private void setupDataTextArea() {
        if (dataTextArea != null) {
            dataTextArea.setWrapText(false);
            dataTextArea.setEditable(false);
        }
    }

    @FXML
    private void handleLoadTable() {
        String selectedTable = tableSelector.getValue();
        if (selectedTable == null || selectedTable.isEmpty()) {
            showError("Please select a table first");
            return;
        }

        loadTableData(selectedTable);
    }

    @FXML
    private void handleRefreshTable() {
        String selectedTable = tableSelector.getValue();
        if (selectedTable != null && !selectedTable.isEmpty()) {
            loadTableData(selectedTable);
        }
    }

    private void loadTableData(String tableName) {
        updateStatus("Loading data from " + tableName + "...");

        Platform.runLater(() -> {
            try {
                clearData();

                String query = "SELECT * FROM " + tableName;

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query);
                     ResultSet rs = stmt.executeQuery()) {

                    // Get column metadata
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    StringBuilder displayText = new StringBuilder();

                    // Create header with column names
                    displayText.append("=".repeat(80)).append("\n");
                    displayText.append(String.format("Table: %s\n", tableName));
                    displayText.append("=".repeat(80)).append("\n\n");

                    // Column headers
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        displayText.append(String.format("%-20s", columnName));
                        if (i < columnCount) displayText.append(" | ");
                    }
                    displayText.append("\n");
                    displayText.append("-".repeat(columnCount * 22)).append("\n");

                    // Load data rows
                    int rowCount = 0;
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            String value = rs.getString(i);
                            String displayValue = (value != null) ? value : "NULL";

                            // Truncate long values for display
                            if (displayValue.length() > 18) {
                                displayValue = displayValue.substring(0, 15) + "...";
                            }

                            displayText.append(String.format("%-20s", displayValue));
                            if (i < columnCount) displayText.append(" | ");
                        }
                        displayText.append("\n");
                        rowCount++;
                    }

                    displayText.append("-".repeat(columnCount * 22)).append("\n");
                    displayText.append(String.format("Total Records: %d\n", rowCount));

                    // Update TextArea
                    dataTextArea.setText(displayText.toString());
                    dataTextArea.positionCaret(0); // Scroll to top

                    // Update UI labels
                    tableNameLabel.setText(tableName + " Table");
                    recordCountLabel.setText(rowCount + " records");
                    refreshButton.setVisible(true);

                    updateStatus("Loaded " + rowCount + " records from " + tableName);
                    updateLastRefreshTime();

                } catch (SQLException e) {
                    e.printStackTrace();
                    showError("Failed to load table data: " + e.getMessage());
                    updateStatus("Error loading table data");
                }

            } catch (Exception e) {
                e.printStackTrace();
                showError("Unexpected error: " + e.getMessage());
                updateStatus("Error occurred while loading data");
            }
        });
    }

    private void clearData() {
        if (dataTextArea != null) {
            dataTextArea.clear();
        }
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            Platform.runLater(() -> statusLabel.setText(message));
        }
    }

    private void updateLastRefreshTime() {
        if (lastUpdatedLabel != null) {
            Platform.runLater(() -> {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                lastUpdatedLabel.setText("Last updated: " + timestamp);
            });
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Panel Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }



    // Getter methods for testing/debugging
    public String getSelectedTable() {
        return tableSelector != null ? tableSelector.getValue() : null;
    }

    public int getRecordCount() {
        if (dataTextArea != null && dataTextArea.getText() != null) {
            String text = dataTextArea.getText();
            if (text.contains("Total Records:")) {
                String[] lines = text.split("\n");
                for (String line : lines) {
                    if (line.startsWith("Total Records:")) {
                        try {
                            return Integer.parseInt(line.split(":")[1].trim());
                        } catch (Exception e) {
                            return 0;
                        }
                    }
                }
            }
        }
        return 0;
    }
}
