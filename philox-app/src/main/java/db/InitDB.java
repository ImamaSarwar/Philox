package db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import utils.PasswordUtil;

public class InitDB {

    public static void createTables(Connection conn) throws SQLException, IOException {
        InputStream input = InitDB.class.getResourceAsStream("/schema.sql");
        /*if (input == null) {
            throw new FileNotFoundException("schema.sql not found in resources!");
        }
*/
        String sql = new String(input.readAllBytes());

        try (Statement stmt = conn.createStatement()) {
            for (String command : sql.split(";")) {
                if (!command.trim().isEmpty()) {
                    stmt.execute(command);
                }
            }
        }

        System.out.println("Tables created successfully!");
    }

    public static void loadDummyData(Connection conn) throws SQLException, IOException {
        
        if (conn == null) {
            return;
        }
        String script = readClasspathResource("/dummy_data.sql");
        if (script == null || script.isBlank()) {
            return;
        }
        int executed = 0;
        try {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
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
            conn.commit();
        } catch (Exception ex) {
            try { conn.rollback(); } catch (SQLException ignored) {}
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    private static String readClasspathResource(String path) {
        try (InputStream is = InitDB.class.getResourceAsStream(path)) {
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

    private static List<String> splitSqlStatements(String script) {
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

    public static void hashDummyPasswords(Connection conn) throws SQLException {
        // Get all users with their current passwords
        String selectSql = "SELECT userId, password FROM USERS";
        String updateSql = "UPDATE USERS SET password = ? WHERE userId = ?";

        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql);
             ResultSet rs = selectStmt.executeQuery()) {

            int updatedCount = 0;
            while (rs.next()) {
                int userId = rs.getInt("userId");
                String currentPassword = rs.getString("password");

                // Hash the current password using PasswordUtil
                String hashedPassword = PasswordUtil.hash(currentPassword);

                // Update the password in the database
                updateStmt.setString(1, hashedPassword);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();

                updatedCount++;
            }

            System.out.println("Successfully hashed " + updatedCount + " passwords!");
        }
    }

    public static boolean isDatabaseEmpty(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM USERS";
        try (PreparedStatement stmt = conn.prepareStatement(checkSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return true; // If we can't check, assume it's empty
    }

    public static void initializeDatabase(Connection conn) throws SQLException, IOException {
        //createTables(conn);
        // Only load dummy data if the database is empty
        if (isDatabaseEmpty(conn)) {
            loadDummyData(conn);
            hashDummyPasswords(conn);
        } else {
            System.out.println("Database already contains data, skipping dummy data loading.");
        }
    }

}
