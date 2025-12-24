package db.repository;

import db.DBConnection;
import db.DataMapper;
import entity.Opportunity;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpportunityRepository {

    public static boolean save(Opportunity opportunity) {
        String sql = "INSERT INTO OPPORTUNITY (organisationId, title, category, description, location, startDate, endDate, closeDate, startTime, duration, capacity, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, opportunity.getOrganisationId());
            pstmt.setString(2, opportunity.getTitle());
            pstmt.setString(3, opportunity.getCategory());
            pstmt.setString(4, opportunity.getDescription());
            pstmt.setString(5, opportunity.getLocation());
            pstmt.setString(6, opportunity.getStartDate() != null ? opportunity.getStartDate().toString() : null);
            pstmt.setString(7, opportunity.getEndDate() != null ? opportunity.getEndDate().toString() : null);
            pstmt.setString(8, opportunity.getCloseDate() != null ? opportunity.getCloseDate().toString() : null);
            pstmt.setString(9, opportunity.getStartTime() != null ? opportunity.getStartTime().toString() : null);
            pstmt.setInt(10, opportunity.getDuration());
            pstmt.setInt(11, opportunity.getCapacity());
            pstmt.setInt(12, opportunity.getStatus());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- NEW METHOD FOR ADMIN ---
    public static List<Opportunity> getAllOpportunities() {
        List<Opportunity> list = new ArrayList<>();
        String sql = "SELECT o.opportunityId, o.organisationId, o.title, o.category, o.description, o.location, " +
                "o.startDate, o.endDate, o.closeDate, o.startTime, o.duration, o.capacity, o.status, o.createdAt, " +
                "c.categoryName, org.mission AS org_mission, u.name AS org_name " +
                "FROM OPPORTUNITY o " +
                "LEFT JOIN OPPORTUNITY_CATEGORY c ON c.categoryId = CAST(o.category AS INTEGER) " +
                "LEFT JOIN ORGANISATION org ON org.organisationId = o.organisationId " +
                "LEFT JOIN USERS u ON u.userId = org.organisationId " +
                "ORDER BY o.createdAt DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(DataMapper.mapOpportunity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Map<Integer, String> getOpportunityCategories() {
        Map<Integer, String> categories = new HashMap<>();
        String sql = "SELECT categoryId, categoryName FROM OPPORTUNITY_CATEGORY ORDER BY categoryName ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categories.put(rs.getInt("categoryId"), rs.getString("categoryName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }


    public static List<Opportunity> getAllOpenOpportunities() {
        List<Opportunity> list = new ArrayList<>();
        String sql = "SELECT o.opportunityId, o.organisationId, o.title, o.category, o.description, o.location, " +
                "o.startDate, o.endDate, o.closeDate, o.startTime, o.duration, o.capacity, o.status, o.createdAt, " +
                "c.categoryName, " +
                "org.organisationId as org_organisationId, org_u.name as org_name, org_u.email as org_email, org.mission as org_mission, " +
                "org.address as org_address, org.website as org_website, org.contactNumber as org_contactNumber, " +
                "org.repName as org_repName, org.repCnic as org_repCnic, " +
                "org.repEmail as org_repEmail, org.repContactNumber as org_repContactNumber " +
                "FROM OPPORTUNITY o " +
                "LEFT JOIN OPPORTUNITY_CATEGORY c ON c.categoryId = CAST(o.category AS INTEGER) " +
                "LEFT JOIN ORGANISATION org ON org.organisationId = o.organisationId " +
                "LEFT JOIN USERS org_u ON org_u.userId = org.organisationId " +
                "WHERE o.status = 1 " +
                "ORDER BY o.createdAt DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(DataMapper.mapOpportunity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static List<Opportunity> getAllOpportunitiesByOrganisation(int organisationId) {
        List<Opportunity> list = new ArrayList<>();
        String sql = "SELECT o.opportunityId, o.organisationId, o.title, o.category, o.description, o.location, " +
                "o.startDate, o.endDate, o.closeDate, o.startTime, o.duration, o.capacity, o.status, o.createdAt, " +
                "c.categoryName " +
                "FROM OPPORTUNITY o " +
                "LEFT JOIN OPPORTUNITY_CATEGORY c ON c.categoryId = CAST(o.category AS INTEGER) " +
                "WHERE o.organisationId = ? " +
                "ORDER BY o.createdAt DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, organisationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(DataMapper.mapOpportunity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean closeOpportunity(int opportunityId) {
        String closeSql = "UPDATE OPPORTUNITY SET status = 0 WHERE opportunityId = ?";
        String rejectApplicationsSql = "UPDATE APPLICATION SET status = -1 WHERE opportunityId = ? AND status = 0";

        Connection conn = null;
        PreparedStatement closeStmt = null;
        PreparedStatement rejectStmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Close the opportunity
            closeStmt = conn.prepareStatement(closeSql);
            closeStmt.setInt(1, opportunityId);
            int opportunityRows = closeStmt.executeUpdate();

            // Reject all pending applications for this opportunity
            rejectStmt = conn.prepareStatement(rejectApplicationsSql);
            rejectStmt.setInt(1, opportunityId);
            int applicationRows = rejectStmt.executeUpdate();

            conn.commit(); // Commit transaction

            if (applicationRows > 0) {
                System.out.println("Rejected " + applicationRows + " pending applications for closed opportunity " + opportunityId);
            }

            return opportunityRows > 0;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (closeStmt != null) closeStmt.close();
                if (rejectStmt != null) rejectStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore auto-commit
                    conn.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public static boolean cancelOpportunity(int opportunityId) {
        String sql = "UPDATE OPPORTUNITY SET status = -1 WHERE opportunityId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, opportunityId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean flagOpportunity(int opportunityId) {

        String flagSql = "UPDATE OPPORTUNITY SET status = -2 WHERE opportunityId = ?";
        String withdrawSql = "UPDATE APPLICATION SET status = -2 WHERE opportunityId = ?";

        Connection conn = null;
        PreparedStatement flagStmt = null;
        PreparedStatement withdrawStmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Flag the opportunity
            flagStmt = conn.prepareStatement(flagSql);
            flagStmt.setInt(1, opportunityId);
            int opportunityRows = flagStmt.executeUpdate();

            // Withdraw all applications for this opportunity
            withdrawStmt = conn.prepareStatement(withdrawSql);
            withdrawStmt.setInt(1, opportunityId);
            int applicationRows = withdrawStmt.executeUpdate();

            conn.commit(); // Commit transaction
            return opportunityRows > 0;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (flagStmt != null) flagStmt.close();
                if (withdrawStmt != null) withdrawStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore auto-commit
                    conn.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public static int closeExpiredOpportunities() {
        String closeOpportunitiesSql = "UPDATE OPPORTUNITY SET status = 0 " +
                "WHERE status = 1 " +
                "AND closeDate < date('now')";

        String rejectApplicationsSql = "UPDATE APPLICATION SET status = -1 " +
                "WHERE status = 0 " +
                "AND opportunityId IN (" +
                "  SELECT opportunityId FROM OPPORTUNITY " +
                "  WHERE status = 0 AND closeDate < date('now')" +
                ")";

        Connection conn = null;
        PreparedStatement closeStmt = null;
        PreparedStatement rejectStmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Close expired opportunities
            closeStmt = conn.prepareStatement(closeOpportunitiesSql);
            int closedOpportunities = closeStmt.executeUpdate();

            // Reject pending applications for the closed opportunities
            rejectStmt = conn.prepareStatement(rejectApplicationsSql);
            int rejectedApplications = rejectStmt.executeUpdate();

            conn.commit(); // Commit transaction

            System.out.println("Closed " + closedOpportunities + " expired opportunities.");
            if (rejectedApplications > 0) {
                System.out.println("Rejected " + rejectedApplications + " pending applications for expired opportunities.");
            }

            return closedOpportunities;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (closeStmt != null) closeStmt.close();
                if (rejectStmt != null) rejectStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore auto-commit
                    conn.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }
}