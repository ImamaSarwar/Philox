package com.example.philoxapp;

import db.DBConnection;
import db.DBUtil;
import db.InitDB;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SimpleAdminTest extends Application {

    private ComboBox<String> tableComboBox;
    private TextArea dataTextArea;
    private Label statusLabel;
    private Connection connection;

    @Override
    public void start(Stage primaryStage) {
        // Create UI components
        Label titleLabel = new Label("Simple Database Admin Test");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        tableComboBox = new ComboBox<>();
        tableComboBox.setPromptText("Select a table...");
        tableComboBox.setPrefWidth(200);

        Button loadTablesButton = new Button("Load Tables");
        loadTablesButton.setOnAction(e -> loadTables());

        Button showDataButton = new Button("Show Data");
        showDataButton.setOnAction(e -> showTableData());

        Button createTablesButton = new Button("Create Tables");
        createTablesButton.setOnAction(e -> createTables());

        Button resetDbButton = new Button("Reset Database");
        resetDbButton.setOnAction(e -> resetDatabase());

        Button fillCategoriesButton = new Button("Fill Opportunity Categories");
        fillCategoriesButton.setOnAction(e -> fillOpportunityCategories());

        Button insertDummyButton = new Button("Insert Dummy Data");
        insertDummyButton.setOnAction(e -> insertDummyData());

        Button clearTableButton = new Button("Clear Selected Table");
        clearTableButton.setOnAction(e -> clearSelectedTable());

        Button deleteTableButton = new Button("Delete Selected Table");
        deleteTableButton.setOnAction(e -> deleteSelectedTable());

        dataTextArea = new TextArea();
        dataTextArea.setPrefRowCount(15);
        dataTextArea.setEditable(false);

        statusLabel = new Label("Ready");

        // Layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(
            titleLabel,
            new Label("1. Click 'Load Tables' to see available tables"),
            loadTablesButton,
            createTablesButton,
            fillCategoriesButton,
            insertDummyButton,
            resetDbButton,
            clearTableButton,
            deleteTableButton,
            new Label("2. Select a table and click 'Show Data'"),
            tableComboBox,
            showDataButton,
            new Label("Data:"),
            dataTextArea,
            statusLabel
        );

        Scene scene = new Scene(root, 650, 520);
        primaryStage.setTitle("Simple Admin Test");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize connection
        initConnection();
    }

    private void initConnection() {
        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                statusLabel.setText("Connected to database");
            } else {
                statusLabel.setText("Failed to connect to database");
            }
        } catch (Exception e) {
            statusLabel.setText("Connection error: " + e.getMessage());
        }
    }

    private void loadTables() {
        if (connection == null) {
            statusLabel.setText("No database connection");
            return;
        }

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            ObservableList<String> tableNames = FXCollections.observableArrayList();
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                if (!tableName.startsWith("sys") && !tableName.startsWith("INFORMATION_SCHEMA")) {
                    tableNames.add(tableName);
                }
            }

            tableComboBox.setItems(tableNames);
            statusLabel.setText("Found " + tableNames.size() + " tables");

        } catch (SQLException e) {
            statusLabel.setText("Error loading tables: " + e.getMessage());
        }
    }

    private void showTableData() {
        String selectedTable = tableComboBox.getValue();
        if (selectedTable == null) {
            statusLabel.setText("Please select a table first");
            return;
        }

        if (connection == null) {
            statusLabel.setText("No database connection");
            return;
        }

        try {
            String query = "SELECT * FROM " + selectedTable;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            StringBuilder data = new StringBuilder();

            // Add column headers
            for (int i = 1; i <= columnCount; i++) {
                data.append(metaData.getColumnName(i));
                if (i < columnCount) data.append(" | ");
            }
            data.append("\n");
            data.append("-".repeat(data.length() - 1)).append("\n");

            // Add data rows
            int rowCount = 0;
            while (resultSet.next() && rowCount < 100) { // Limit to 100 rows for testing
                for (int i = 1; i <= columnCount; i++) {
                    String value = resultSet.getString(i);
                    data.append(value != null ? value : "NULL");
                    if (i < columnCount) data.append(" | ");
                }
                data.append("\n");
                rowCount++;
            }

            dataTextArea.setText(data.toString());
            statusLabel.setText("Showing " + rowCount + " rows from " + selectedTable);

        } catch (SQLException e) {
            statusLabel.setText("Error loading data: " + e.getMessage());
            dataTextArea.setText("Error: " + e.getMessage());
        }
    }

    private void resetDatabase() {
        try {
            if (connection == null) {
                initConnection();
            }
            DBUtil.resetDatabase(connection); // keep connection open
            loadTables();
            dataTextArea.clear();
            statusLabel.setText("Database reset successfully");
        } catch (Exception e) {
            statusLabel.setText("Error resetting database: " + e.getMessage());
        }
    }

    private void fillOpportunityCategories() {
        String[] categories = {
            "Environmental",
            "Education",
            "Healthcare",
            "Community Support",
            "Animal Welfare",
            "Arts & Culture",
            "Other"
        };
        if (connection == null) {
            statusLabel.setText("No database connection");
            return;
        }
        int inserted = 0;
        try {
            for (String category : categories) {
                String sql = "INSERT OR IGNORE INTO OPPORTUNITY_CATEGORY (categoryName) VALUES (?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, category);
                    int result = pstmt.executeUpdate();
                    if (result > 0) inserted++;
                }
            }
            statusLabel.setText("Inserted " + inserted + " new categories (existing ones ignored)");
            loadTables();
        } catch (SQLException e) {
            statusLabel.setText("Error inserting categories: " + e.getMessage());
        }
    }

    private void clearSelectedTable() {
        String selectedTable = tableComboBox.getValue();
        if (selectedTable == null || selectedTable.isEmpty()) {
            statusLabel.setText("Please select a table to clear.");
            return;
        }
        if (connection == null) {
            statusLabel.setText("No database connection");
            return;
        }
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM " + selectedTable);
            statusLabel.setText("Cleared all rows from table: " + selectedTable);
            showTableData();
        } catch (SQLException e) {
            statusLabel.setText("Error clearing table: " + e.getMessage());
        }
    }

    private void deleteSelectedTable() {
        String selectedTable = tableComboBox.getValue();
        if (selectedTable == null || selectedTable.isEmpty()) {
            statusLabel.setText("Please select a table to delete.");
            return;
        }
        if (connection == null) {
            statusLabel.setText("No database connection");
            return;
        }
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS " + selectedTable);
            statusLabel.setText("Deleted table: " + selectedTable);
            loadTables();
            dataTextArea.clear();
        } catch (SQLException e) {
            statusLabel.setText("Error deleting table: " + e.getMessage());
        }
    }

    private void insertDummyData() {
        if (connection == null) {
            statusLabel.setText("No database connection");
            return;
        }
        String script = readClasspathResource("/dummy_data.sql");
        if (script == null || script.isBlank()) {
            statusLabel.setText("Could not load dummy_data.sql from resources");
            return;
        }
        int executed = 0;
        try {
            connection.setAutoCommit(false);
            try (Statement stmt = connection.createStatement()) {
                for (String statement : splitSqlStatements(script)) {
                    if (statement.isBlank()) continue;
                    try {
                        stmt.executeUpdate(statement);
                        executed++;
                    } catch (SQLException ex) {
                        // ignore duplicates for convenience, report others
                        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unique")) {
                            // skip
                        } else {
                            throw ex;
                        }
                    }
                }
            }
            connection.commit();
            statusLabel.setText("Inserted dummy data. Executed " + executed + " statements.");
            loadTables();
        } catch (Exception ex) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            statusLabel.setText("Error inserting dummy data: " + ex.getMessage());
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    private void createTables() {
        if (connection == null) {
            statusLabel.setText("No database connection");
            return;
        }
        String script = readClasspathResource("/schema.sql");
        if (script == null || script.isBlank()) {
            statusLabel.setText("Could not load schema.sql from resources");
            return;
        }
        int executed = 0;
        try {
            connection.setAutoCommit(false);
            try (Statement stmt = connection.createStatement()) {
                for (String statement : splitSqlStatements(script)) {
                    if (statement.isBlank()) continue;
                    stmt.executeUpdate(statement);
                    executed++;
                }
            }
            connection.commit();
            statusLabel.setText("Created/verified tables. Executed " + executed + " statements.");
            loadTables();
        } catch (Exception ex) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            statusLabel.setText("Error creating tables: " + ex.getMessage());
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    private String readClasspathResource(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) return null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                return sb.toString();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> splitSqlStatements(String script) {
        // Remove line comments and split on semicolons
        StringBuilder cleaned = new StringBuilder();
        for (String line : script.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("--") || trimmed.startsWith("//")) continue;
            cleaned.append(line).append('\n');
        }
        List<String> statements = new ArrayList<>();
        for (String part : cleaned.toString().split(";")) {
            String stmt = part.trim();
            if (!stmt.isEmpty()) statements.add(stmt);
        }
        return statements;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
