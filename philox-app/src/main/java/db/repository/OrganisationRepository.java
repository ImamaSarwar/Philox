package db.repository;

import entity.Organisation;
import db.DBConnection;
import service.DocumentStorageService;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class OrganisationRepository {

    public static boolean save(Organisation organisation) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int userId = UserRepository.saveUser(conn,organisation);
            organisation.setOrganisationId(userId);

            String orgSql = "INSERT INTO ORGANISATION(organisationId, repName, repCnic, repEmail, repContactNumber, contactNumber, mission, address, website) VALUES (?, ?, ?, ? ,?, ?, ?, ?, ?)";
            try (PreparedStatement orgStmt = conn.prepareStatement(orgSql)) {
                orgStmt.setInt(1, userId);
                orgStmt.setString(2, organisation.getRepName());
                orgStmt.setString(3, organisation.getRepCnic());
                orgStmt.setString(4, organisation.getRepEmail());
                orgStmt.setString(5, organisation.getRepContactNumber());
                orgStmt.setString(6, organisation.getContactNumber());
                orgStmt.setString(7, organisation.getMission());
                orgStmt.setString(8, organisation.getAddress());
                orgStmt.setString(9, organisation.getWebsite());
                int affectedOrgRows = orgStmt.executeUpdate();
                if (affectedOrgRows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            String registrationProofPath = DocumentStorageService.saveFile(organisation.getRegistrationProofPath(),userId);
            String taxDocumentPath = DocumentStorageService.saveFile(organisation.getTaxDocumentPath(),userId);
            String cnicProofPath = DocumentStorageService.saveFile(organisation.getCnicProofPath(),userId);
            organisation.setRegistrationProofPath(registrationProofPath);
            organisation.setTaxDocumentPath(taxDocumentPath);
            organisation.setCnicProofPath(cnicProofPath);

            if (!insertLicense(conn, userId, organisation.getRegistrationAuthority(), organisation.getRegistrationNumber(), organisation.getIssueDate(),
                    organisation.getNtn(), organisation.getRegistrationProofPath(), organisation.getTaxDocumentPath(), organisation.getCnicProofPath())) {
                conn.rollback();
                return false;
            }

            if (!insertSocialMedia(conn, userId, organisation.getInstagramLink(), organisation.getFacebookLink(), organisation.getLinkedInLink())) {
                conn.rollback();
                return false;
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

    // --- NEW METHODS FOR ADMIN DASHBOARD ---

    public static List<Organisation> getAllOrganisations() {
        List<Organisation> list = new ArrayList<>();
        String sql = "SELECT o.*, u.name, u.email, u.status, u.type, u.registrationDate, l.*, sm.* " +
                "FROM ORGANISATION o " +
                "JOIN USERS u ON o.organisationId = u.userId " +
                "LEFT JOIN ORGANISATION_LICENSE l ON l.organisationId = o.organisationId " +
                "LEFT JOIN ORGANISATION_SOCIAL_MEDIA sm ON sm.organisationId = o.organisationId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Organisation org = new Organisation();
                org.setOrganisationId(rs.getInt("organisationId"));
                org.setName(rs.getString("name"));
                org.setEmail(rs.getString("email"));
                org.setStatus(rs.getInt("status"));

                String regDateStr = rs.getString("registrationDate");
                if (regDateStr != null) {
                    try { org.setRegistrationDate(LocalDate.parse(regDateStr.split(" ")[0])); } catch(Exception e){}
                }

                org.setMission(rs.getString("mission"));
                org.setAddress(rs.getString("address"));
                org.setContactNumber(rs.getString("contactNumber"));
                org.setWebsite(rs.getString("website"));

                org.setRepName(rs.getString("repName"));
                org.setRepCnic(rs.getString("repCnic"));
                org.setRepContactNumber(rs.getString("repContactNumber"));
                org.setRepEmail(rs.getString("repEmail"));

                org.setNtn(rs.getString("ntn"));
                org.setRegistrationNumber(rs.getString("registrationNumber"));
                org.setRegistrationAuthority(rs.getString("registrationAuthority"));

                org.setRegistrationProofPath(rs.getString("registrationProofPath"));
                org.setTaxDocumentPath(rs.getString("taxDocumentPath"));
                org.setCnicProofPath(rs.getString("cnicProofPath"));

                org.setFacebookLink(rs.getString("facebookLink"));
                org.setInstagramLink(rs.getString("instagramLink"));
                org.setLinkedInLink(rs.getString("linkedInLink"));

                list.add(org);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean updateStatus(int organisationId, int status) {
        String sql = "UPDATE USERS SET status = ? WHERE userId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, status);
            pstmt.setInt(2, organisationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateOrganisationProfile(int organisationId,String mission, String contact, String address, String website,
                                                    String repName, String repContact,String repCnic, String repEmail) {
        String orgSql = "UPDATE ORGANISATION SET mission = ?, address = ?, website = ?, repName = ?, repCnic = ?, repEmail = ?, " +
                "repContactNumber = ?, contactNumber = ? WHERE organisationId = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(orgSql)) {
                stmt.setString(1,mission);
                stmt.setString(2, address);
                stmt.setString(3, website);
                stmt.setString(4, repName);
                stmt.setString(5, repCnic);
                stmt.setString(6, repEmail);
                stmt.setString(7, repContact);
                stmt.setString(8, contact);
                stmt.setInt(9, organisationId);
                stmt.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if(conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    private static boolean insertSocialMedia(Connection conn,int organisationId, String instagram, String facebook, String linkedIn) {
        String sql = "INSERT INTO ORGANISATION_SOCIAL_MEDIA (organisationId, instagramLink, facebookLink, linkedInLink) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, organisationId);
            pstmt.setString(2, instagram);
            pstmt.setString(3, facebook);
            pstmt.setString(4, linkedIn);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insertLicense(Connection conn,int organisationId, String registrationAuthority, String registrationNumber, LocalDate issueDate,
                                        String ntn, String registrationProofPath, String taxDocumentPath, String cnicProofPath) {
        String sql = "INSERT INTO ORGANISATION_LICENSE (organisationId, registrationAuthority, registrationNumber, issueDate,ntn, registrationProofPath,taxDocumentPath,cnicProofPath) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, organisationId);
            pstmt.setString(2, registrationAuthority);
            pstmt.setString(3, registrationNumber);
            pstmt.setString(4, issueDate != null ? issueDate.toString() : null);
            pstmt.setString(5, ntn);
            pstmt.setString(6, registrationProofPath);
            pstmt.setString(7, taxDocumentPath);
            pstmt.setString(8, cnicProofPath);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean existsByRegistrationNumber(String registrationNumber) {
        String sql = "SELECT 1 FROM ORGANISATION_LICENSE WHERE registrationNumber = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, registrationNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean existsByNtn(String ntn) {
        String sql = "SELECT 1 FROM ORGANISATION_LICENSE WHERE ntn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ntn);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateRating(int organisationId, double rating) {
        String sql = "UPDATE ORGANISATION SET rating = ? WHERE organisationId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, rating);
            pstmt.setInt(2, organisationId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Map<String, Integer> getOrganisationStats(int organisationId) {
        Map<String, Integer> stats = new java.util.HashMap<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            // 1. Open opportunities (status = 1)
            String openOppSql = "SELECT COUNT(*) FROM OPPORTUNITY WHERE organisationId = ? AND status = 1";
            try (PreparedStatement stmt = conn.prepareStatement(openOppSql)) {
                stmt.setInt(1, organisationId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) stats.put("openOpportunities", rs.getInt(1));
                }
            }
            // 2. Applications pending review (status = 0)
            String pendingAppSql = "SELECT COUNT(*) FROM APPLICATION a JOIN OPPORTUNITY o ON a.opportunityId = o.opportunityId WHERE o.organisationId = ? AND a.status = 0";
            try (PreparedStatement stmt = conn.prepareStatement(pendingAppSql)) {
                stmt.setInt(1, organisationId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) stats.put("applicationsPendingReview", rs.getInt(1));
                }
            }
            // 3. Total opportunities posted
            String totalOppSql = "SELECT COUNT(*) FROM OPPORTUNITY WHERE organisationId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(totalOppSql)) {
                stmt.setInt(1, organisationId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) stats.put("totalOpportunitiesPosted", rs.getInt(1));
                }
            }
            // 4. Total volunteers engaged (distinct volunteers with accepted applications, status = 1)
            String engagedVolSql = "SELECT COUNT(DISTINCT a.volunteerId) FROM APPLICATION a JOIN OPPORTUNITY o ON a.opportunityId = o.opportunityId WHERE o.organisationId = ? AND a.status = 1";
            try (PreparedStatement stmt = conn.prepareStatement(engagedVolSql)) {
                stmt.setInt(1, organisationId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) stats.put("totalVolunteersEngaged", rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return stats;
    }

    public static boolean approveOrganisation(int organisationId) {
        //set status to 1 in users table
        String sql = "UPDATE USERS SET status = 1 WHERE userId = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, organisationId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean rejectOrganisation(int organisationId) {
        // set status to -1 in users table
        String sql = "UPDATE USERS SET status = -1 WHERE userId = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, organisationId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

