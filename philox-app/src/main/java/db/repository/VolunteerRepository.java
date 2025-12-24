package db.repository;

import entity.Volunteer;
import db.DBConnection;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class VolunteerRepository {
    public static boolean save(Volunteer volunteer) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Save user data first and get the generated user ID
            int userId = UserRepository.saveUser(conn,volunteer);
            volunteer.setVolunteerId(userId);

            // Save volunteer-specific data
            String volunteerSql = "INSERT INTO VOLUNTEER(volunteerId, phone, cnic, age, city) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement volunteerStmt = conn.prepareStatement(volunteerSql)) {
                volunteerStmt.setInt(1, userId);
                volunteerStmt.setString(2, volunteer.getPhone());
                volunteerStmt.setString(3, volunteer.getCnic());
                volunteerStmt.setInt(4, volunteer.getAge());
                volunteerStmt.setString(5, volunteer.getCity());
                int affectedVolunteerRows = volunteerStmt.executeUpdate();
                if (affectedVolunteerRows == 0) {
                    conn.rollback();
                    return false;
                }
            }
            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public static boolean existsByCnic(String cnic) {
        String sql = "SELECT 1 FROM VOLUNTEER WHERE cnic = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cnic);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateVolunteerInfo(int volunteerId, String name, String phone, String cnic, int age, String city) {
        boolean userUpdated = UserRepository.updateUserName(volunteerId, name);
        if (!userUpdated) return false;
        System.out.println("User basic info updated successfully.");
        String sql = "UPDATE VOLUNTEER SET phone = ?, cnic = ?, age = ?, city = ? WHERE volunteerId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, cnic);
            pstmt.setInt(3, age);
            pstmt.setString(4, city);
            pstmt.setInt(5, volunteerId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException  e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateVolunteerPortfolio(int volunteerId, String bio, List<String> skills) {
        String sql = "UPDATE VOLUNTEER SET bio = ?, skills = ? WHERE volunteerId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bio);
            String skillsText = String.join(",", skills);
            pstmt.setString(2, skillsText);
            pstmt.setInt(3, volunteerId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // volunteer stats: applications (total,pending,compleeted)
    public static Map<String, Integer> getVolunteerStats(int volunteerId) {
        Map<String, Integer> stats = new java.util.HashMap<>();
        int total = 0, completed = 0, pending = 0;
        String sql = "SELECT status FROM APPLICATION WHERE volunteerId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, volunteerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    total++;
                    int status = rs.getInt("status");
                    if (status == 1) completed++;
                    else if (status == 0) pending++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        stats.put("total", total);
        stats.put("completed", completed);
        stats.put("pending", pending);
        return stats;
    }

    public static boolean updateRating(int volunteerId, double rating) {
        String sql = "UPDATE VOLUNTEER SET rating = ? WHERE volunteerId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, rating);
            pstmt.setInt(2, volunteerId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
