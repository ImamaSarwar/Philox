package db.repository;

import db.DBConnection;
import entity.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AdminRepository {

    public static boolean save(Admin admin) {
        Connection conn = null;
        PreparedStatement userStmt = null;
        PreparedStatement adminStmt = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Transaction

            int userId = UserRepository.saveUser(conn,admin);
            admin.setUserId(userId);

            String adminSql = "INSERT INTO ADMIN (adminId, role) VALUES (?, ?)";
            adminStmt = conn.prepareStatement(adminSql);
            adminStmt.setInt(1, userId);
            adminStmt.setString(2, admin.getRole());
            int adminRows = adminStmt.executeUpdate();
            if (adminRows == 0) {
                conn.rollback();
                return false;
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignore) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (userStmt != null) userStmt.close(); } catch (Exception ignore) {}
            try { if (adminStmt != null) adminStmt.close(); } catch (Exception ignore) {}
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (Exception ignore) {}
        }
    }

    public static int getPendingOrganisationApprovalsCount() {
        String sql = "SELECT COUNT(*) FROM USERS WHERE type = 1 AND status = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getTotalBadgesCount() {
        String sql = "SELECT COUNT(*) FROM BADGE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getTotalActiveUsersCount() {
        String sql = "SELECT COUNT(*) FROM USERS WHERE status = 1 AND type != 3";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
