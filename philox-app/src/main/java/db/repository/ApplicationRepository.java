package db.repository;

import db.DBConnection;
import db.DataMapper;
import entity.Application;
import entity.Opportunity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ApplicationRepository {

    public static boolean save(Application application){
        String sql = "INSERT INTO APPLICATION (applicationComment, opportunityId, volunteerId,  status) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, application.getApplicationComment());
            pstmt.setInt(2, application.getOpportunityId());
            pstmt.setInt(3, application.getVolunteerId());
            pstmt.setInt(4, application.getStatus());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Application> getApplicationsForOrganisation(int organisationId) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT a.applicationId, a.applicationComment, a.opportunityId, a.volunteerId, " +
                "a.createdAt as applicationCreatedAt, a.status as applicationStatus, " +
                "v.volunteerId as vol_volunteerId, v.phone as vol_phone, v.cnic as vol_cnic, " +
                "v.age as vol_age, v.city as vol_city, v.availability as vol_availability, " +
                "v.rating as vol_rating, v.skills as vol_skills, v.bio as vol_bio, " +
                "u.name as vol_name, u.email as vol_email, u.status as vol_status, " +
                "o.opportunityId as opp_opportunityId, o.organisationId as opp_organisationId, " +
                "o.title as opp_title, o.category as opp_category, o.description as opp_description, " +
                "o.location as opp_location, o.startDate as opp_startDate, o.endDate as opp_endDate, " +
                "o.closeDate as opp_closeDate, o.startTime as opp_startTime, o.duration as opp_duration, " +
                "o.capacity as opp_capacity, o.status as opp_status, o.createdAt as opp_createdAt, " +
                "org.organisationId as org_organisationId, org_u.name as org_name, org_u.email as org_email, org.mission as org_mission, " +
                "org.address as org_address, org.website as org_website, org.contactNumber as org_contactNumber, " +
                "org.repName as org_repName, org.repCnic as org_repCnic, " +
                "org.repEmail as org_repEmail, org.repContactNumber as org_repContactNumber " +
                "FROM APPLICATION a " +
                "JOIN OPPORTUNITY o ON a.opportunityId = o.opportunityId " +
                "JOIN ORGANISATION org ON o.organisationId = org.organisationId " +
                "JOIN USERS org_u ON org.organisationId = org_u.userId " +
                "JOIN VOLUNTEER v ON a.volunteerId = v.volunteerId " +
                "JOIN USERS u ON v.volunteerId = u.userId " +
                "WHERE o.organisationId = ? ORDER BY a.createdAt DESC";

        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, organisationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(db.DataMapper.mapApplication(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }


    public static List<Application> getApplicationsForOpportunity(int opportunityId) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT a.applicationId, a.applicationComment, a.opportunityId, a.volunteerId, " +
                "a.createdAt as applicationCreatedAt, a.status as applicationStatus, " +
                "v.volunteerId as vol_volunteerId, v.phone as vol_phone, v.cnic as vol_cnic, " +
                "v.age as vol_age, v.city as vol_city, v.availability as vol_availability, " +
                "v.rating as vol_rating, v.skills as vol_skills, v.bio as vol_bio, " +
                "u.name as vol_name, u.email as vol_email, u.status as vol_status, " +
                "o.opportunityId as opp_opportunityId, o.organisationId as opp_organisationId, " +
                "o.title as opp_title, o.category as opp_category, o.description as opp_description, " +
                "o.location as opp_location, o.startDate as opp_startDate, o.endDate as opp_endDate, " +
                "o.closeDate as opp_closeDate, o.startTime as opp_startTime, o.duration as opp_duration, " +
                "o.capacity as opp_capacity, o.status as opp_status, o.createdAt as opp_createdAt " +
                "FROM APPLICATION a " +
                "JOIN OPPORTUNITY o ON a.opportunityId = o.opportunityId " +
                "JOIN VOLUNTEER v ON a.volunteerId = v.volunteerId " +
                "JOIN USERS u ON v.volunteerId = u.userId " +
                "WHERE a.opportunityId = ? ORDER BY a.createdAt DESC";

        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, opportunityId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(db.DataMapper.mapApplication(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    public static List<Application> getApplicationsForVolunteer(int volunteerId) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT a.applicationId, a.applicationComment, a.opportunityId, a.volunteerId, " +
                "a.createdAt as applicationCreatedAt, a.status as applicationStatus, " +
                "v.volunteerId as vol_volunteerId, v.phone as vol_phone, v.cnic as vol_cnic, " +
                "v.age as vol_age, v.city as vol_city, v.availability as vol_availability, " +
                "v.rating as vol_rating, v.skills as vol_skills, v.bio as vol_bio, " +
                "u.name as vol_name, u.email as vol_email, u.status as vol_status, " +
                "o.opportunityId as opp_opportunityId, o.organisationId as opp_organisationId, " +
                "o.title as opp_title, o.category as opp_category, o.description as opp_description, " +
                "o.location as opp_location, o.startDate as opp_startDate, o.endDate as opp_endDate, " +
                "o.closeDate as opp_closeDate, o.startTime as opp_startTime, o.duration as opp_duration, " +
                "o.capacity as opp_capacity, o.status as opp_status, o.createdAt as opp_createdAt, " +
                "org.organisationId as org_organisationId, org_u.name as org_name, org_u.email as org_email, org.mission as org_mission, " +
                "org.address as org_address, org.website as org_website, org.contactNumber as org_contactNumber, " +
                "org.repName as org_repName, org.repCnic as org_repCnic, " +
                "org.repEmail as org_repEmail, org.repContactNumber as org_repContactNumber " +
                "FROM APPLICATION a " +
                "JOIN OPPORTUNITY o ON a.opportunityId = o.opportunityId " +
                "JOIN ORGANISATION org ON o.organisationId = org.organisationId " +
                "JOIN USERS org_u ON org.organisationId = org_u.userId " +
                "JOIN VOLUNTEER v ON a.volunteerId = v.volunteerId " +
                "JOIN USERS u ON v.volunteerId = u.userId " +
                "WHERE a.volunteerId = ? ORDER BY a.createdAt DESC";

        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, volunteerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(db.DataMapper.mapApplication(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    public static boolean updateApplicationStatus(int applicationId, int status) {
        String sql = "UPDATE APPLICATION SET status = ? WHERE applicationId = ?";
        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, status);
            pstmt.setInt(2, applicationId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Opportunity> findCompletedByVolunteer(int volunteerId) {
        List<Opportunity> list = new ArrayList<>();
        String sql = "SELECT o.*, " +
                "org.organisationId AS org_organisationId, u.name AS org_name, u.email AS org_email, org.mission AS org_mission, " +
                "org.address AS org_address, org.website AS org_website, org.contactNumber AS org_contactNumber, " +
                "org.repName AS org_repName, org.repCnic AS org_repCnic, " +
                "org.repEmail AS org_repEmail, org.repContactNumber AS org_repContactNumber " +
                "FROM OPPORTUNITY o " +
                "JOIN APPLICATION a ON a.opportunityId = o.opportunityId " +
                "JOIN ORGANISATION org ON o.organisationId = org.organisationId " +
                "JOIN USERS u ON org.organisationId = u.userId " +
                "WHERE a.volunteerId = ? AND a.status = 1 " +
                "ORDER BY o.endDate DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, volunteerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(DataMapper.mapOpportunity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Completed opportunities for volunteer " + volunteerId + ": " + list.size());
        return list;
    }

    public static int getApplicationCountByOpportunity(int opportunityId) {
        String sql = "SELECT COUNT(*) FROM APPLICATION WHERE opportunityId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, opportunityId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException  e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean hasVolunteerApplied(int volunteerId, int opportunityId) {
        String sql = "SELECT COUNT(*) FROM APPLICATION WHERE volunteerId = ? AND opportunityId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, volunteerId);
            pstmt.setInt(2, opportunityId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int finalizeExpiredApplications() {
        String sql = "UPDATE APPLICATION SET status = 2 " +
                "WHERE status = 1 " +
                "AND opportunityId IN (" +
                "  SELECT opportunityId FROM OPPORTUNITY " +
                "  WHERE endDate < date('now')" +
                ")";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int updatedRows = pstmt.executeUpdate();
            System.out.println("Finalized " + updatedRows + " expired applications.");
            return updatedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
