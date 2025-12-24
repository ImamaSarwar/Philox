package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBUtil {
    // Returns the next available primary key for a table
    public static int getNextPrimaryKey(String tableName, String pkColumn) {
        String sql = "SELECT MAX(" + pkColumn + ") FROM " + tableName;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            int maxId = rs.next() ? rs.getInt(1) : 0;
            return maxId + 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 1; // fallback to 1 if error
        }
    }

    // FOR TESTING PURPOSES ONLY
    // Drops all tables in the database (reset DB) using an existing, open connection.
    // This method does NOT close the provided connection.
    public static void resetDatabase(Connection conn) {
        if (conn == null) return;
        boolean originalAutoCommit = true;
        try (Statement stmt = conn.createStatement()) {
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // Disable foreign keys to avoid drop order issues
            stmt.execute("PRAGMA foreign_keys = OFF");

            // Collect all non-system tables first
            List<String> tablesToDrop = new ArrayList<>();
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    if (tableName == null) continue;
                    String lower = tableName.toLowerCase();
                    if (!lower.startsWith("sqlite_")) { // skip SQLite system tables
                        tablesToDrop.add(tableName);
                    }
                }
            }

            // Drop each table
            for (String table : tablesToDrop) {
                try (PreparedStatement drop = conn.prepareStatement("DROP TABLE IF EXISTS " + table)) {
                    drop.executeUpdate();
                }
            }

            // Reset autoincrement counters (sqlite_sequence) if present
            try {
                stmt.executeUpdate("DELETE FROM sqlite_sequence");
            } catch (SQLException ignore) {
                // sqlite_sequence may not exist yet; ignore
            }

            // Re-enable foreign keys
            stmt.execute("PRAGMA foreign_keys = ON");

            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
        } finally {
            try { conn.setAutoCommit(originalAutoCommit); } catch (SQLException ignored) {}
        }
    }

    // Existing variant that manages its own connection lifecycle.
    public static void resetDatabase() {
        try (Connection conn = DBConnection.getConnection()) {
            resetDatabase(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
