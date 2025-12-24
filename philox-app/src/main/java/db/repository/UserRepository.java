package db.repository;

import entity.User;
import db.DBConnection;

import java.sql.*;

public class UserRepository {

    public static int saveUser(Connection conn,User user) throws SQLException {
        String sql = "INSERT INTO USERS(name, email, password, status, type, registrationDate) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setInt(4, user.getStatus());
            pstmt.setInt(5, getUserType(user));
            pstmt.setString(6, user.getRegistrationDate() != null ? user.getRegistrationDate().toString() : null);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            // Get the generated user ID
            try (PreparedStatement idStmt = conn.prepareStatement("SELECT last_insert_rowid()")) {
                try (ResultSet rs = idStmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
        }
    }

    public static boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM USERS WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static int getUserType(User user) {
        // Determine user type based on class type
        String className = user.getClass().getSimpleName();
        switch (className) {
            case "Volunteer":
                return 2;
            case "Organisation":
                return 1;
            default:
                return 0; // Default user type
        }
    }

    public static boolean updateUserName(int userId,String name) {
        String sql = "UPDATE USERS SET name = ? WHERE userId = ?";
        System.out.println("Updating user name to: " + name + " for userId: " + userId);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE USERS SET password = ? WHERE userId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteUser(int userId) {
        String sql = "DELETE FROM USERS WHERE userId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getPasswordByUserId(int userId) {
        String sql = "SELECT password FROM USERS WHERE userId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
