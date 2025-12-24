package db;

import entity.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DataMapper {

    private static final DateTimeFormatter APP_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static User getAdminByEmail(String email) {
        // join w admin
        String sql = "SELECT a.*, u.* FROM ADMIN a JOIN USERS u ON a.adminId = u.userId WHERE u.email = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setUserId(rs.getInt("adminId"));
                    admin.setRole(rs.getString("role"));
                    admin.setName(rs.getString("name"));
                    admin.setEmail(rs.getString("email"));
                    admin.setStatus(rs.getInt("status"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static Volunteer getVolunteerByEmail(String email) {
        String sql = "SELECT v.*, u.* FROM VOLUNTEER v JOIN USERS u ON v.volunteerId = u.userId WHERE u.email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Volunteer volunteer = new Volunteer();
                    volunteer.setVolunteerId(rs.getInt("volunteerId"));
                    volunteer.setPhone(rs.getString("phone"));
                    volunteer.setCnic(rs.getString("cnic"));
                    volunteer.setAge(rs.getInt("age"));
                    volunteer.setCity(rs.getString("city"));
                    volunteer.setAvailability(rs.getInt("availability"));
                    volunteer.setBio(rs.getString("bio"));
                    volunteer.setRating(rs.getFloat("rating"));
                    volunteer.setName(rs.getString("name"));
                    volunteer.setEmail(rs.getString("email"));
                    volunteer.setStatus(rs.getInt("status"));
                    String skills = rs.getString("skills");
                    if (skills != null) {
                        volunteer.setSkills(java.util.Arrays.asList(skills.split(",")));
                    }
                    return volunteer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Organisation getOrganisationByEmail(String email) {
        String sql = "SELECT o.*, u.*, l.*, sm.* " +
                "FROM ORGANISATION o " +
                "JOIN USERS u ON o.organisationId = u.userId " +
                "LEFT JOIN ORGANISATION_LICENSE l ON l.organisationId = o.organisationId " +
                "LEFT JOIN ORGANISATION_SOCIAL_MEDIA sm ON sm.organisationId = o.organisationId " +
                "WHERE u.email = ?";
        return getOrganisationByQuery(sql, email);
    }

    private static Organisation getOrganisationByQuery(String sql, String param) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, param);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Organisation org = new Organisation();
                    org.setOrganisationId(rs.getInt("organisationId"));
                    org.setName(rs.getString("name"));
                    org.setEmail(rs.getString("email"));
                    org.setStatus(rs.getInt("status"));
                    org.setWebsite(rs.getString("website"));
                    org.setRepName(rs.getString("repName"));
                    org.setRepCnic(rs.getString("repCnic"));
                    org.setRepContactNumber(rs.getString("repContactNumber"));
                    org.setRepEmail(rs.getString("repEmail"));
                    org.setContactNumber(rs.getString("contactNumber"));
                    org.setMission(rs.getString("mission"));
                    org.setAddress(rs.getString("address"));
                    org.setRegistrationAuthority(rs.getString("registrationAuthority"));
                    org.setRegistrationNumber(rs.getString("registrationNumber"));
                    String issueDateStr = rs.getString("issueDate");
                    org.setIssueDate(issueDateStr != null ? LocalDate.parse(issueDateStr) : null);
                    org.setNtn(rs.getString("ntn"));
                    org.setRegistrationProofPath(rs.getString("registrationProofPath"));
                    org.setTaxDocumentPath(rs.getString("taxDocumentPath"));
                    org.setCnicProofPath(rs.getString("cnicProofPath"));
                    org.setInstagramLink(rs.getString("instagramLink"));
                    org.setFacebookLink(rs.getString("facebookLink"));
                    org.setLinkedInLink(rs.getString("linkedInLink"));
                    return org;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ... [Rest of existing methods: mapOpportunity, mapApplication, etc.] ...
    // Data mapper for Opportunity
    private static final DateTimeFormatter TIME_AMPM = DateTimeFormatter.ofPattern("hh:mm a");
    public static Opportunity mapOpportunity(ResultSet rs) throws SQLException {
        Opportunity opp = new Opportunity();
        opp.setOpportunityId(rs.getInt("opportunityId"));
        opp.setOrganisationId(rs.getInt("organisationId"));
        opp.setTitle(rs.getString("title"));
        try {
            // Try to get categoryName from joined table first, fallback to category column
            String categoryName = null;
            try {
                categoryName = rs.getString("categoryName");
            } catch (SQLException ignore) {}

            if (categoryName != null && !categoryName.isEmpty()) {
                opp.setCategory(categoryName);
            } else {
                opp.setCategory(rs.getString("category"));
            }
        } catch (SQLException ignore) {}
        opp.setDescription(rs.getString("description"));
        opp.setLocation(rs.getString("location"));
        String s = rs.getString("startDate");
        if (s != null && !s.isEmpty()) {
            try { opp.setStartDate(LocalDate.parse(s)); } catch (Exception ignore) { }
        }
        s = rs.getString("endDate");
        if (s != null && !s.isEmpty()) {
            try { opp.setEndDate(LocalDate.parse(s)); } catch (Exception ignore) { }
        }
        s = rs.getString("closeDate");
        if (s != null && !s.isEmpty()) {
            try { opp.setCloseDate(LocalDate.parse(s)); } catch (Exception ignore) { }
        }
        s = rs.getString("startTime");
        if (s != null && !s.isEmpty()) {
            try {
                opp.setStartTime(LocalTime.parse(s));
            } catch (Exception ex) {
                try { opp.setStartTime(LocalTime.parse(s, TIME_AMPM)); } catch (Exception ignore) { }
            }
        }
        try { opp.setDuration(rs.getInt("duration")); } catch (SQLException ignore) { }
        try { opp.setCapacity(rs.getInt("capacity")); } catch (SQLException ignore) { }
        try { opp.setStatus(rs.getInt("status")); } catch (SQLException ignore) { }
        try {
            String createdAtStr = rs.getString("createdAt");
            if (createdAtStr != null && !createdAtStr.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                opp.setCreatedAt(java.time.LocalDateTime.parse(createdAtStr, formatter));
            }
        } catch (SQLException ignore) { }

        // Map basic organisation info if the columns are present (either original or aliased)
        try {
            int orgId = 0;
            // direct column (when selecting from OPPORTUNITY alone)
            if (hasColumn(rs, "organisationId")) {
                orgId = rs.getInt("organisationId");
            }
            // aliased columns from JOIN (e.g., org_organisationId)
            if (hasColumn(rs, "org_organisationId")) {
                orgId = rs.getInt("org_organisationId");
            }
            if (orgId > 0) {
                Organisation org = new Organisation();
                org.setOrganisationId(orgId);

                // Handle organisationName from the getAllOpportunities JOIN query
                if (hasColumn(rs, "org_name")) {
                    org.setName(rs.getString("org_name"));
                }
                if (org.getName() == null && hasColumn(rs, "name")) {
                    org.setName(rs.getString("name"));
                }

                // Map organisation email (from USERS table)
                if (hasColumn(rs, "org_email")) org.setEmail(rs.getString("org_email"));
                if (hasColumn(rs, "org_mission")) org.setMission(rs.getString("org_mission"));
                if (hasColumn(rs, "org_address")) org.setAddress(rs.getString("org_address"));
                if (hasColumn(rs, "org_website")) org.setWebsite(rs.getString("org_website"));
                if (hasColumn(rs, "org_contactNumber")) org.setContactNumber(rs.getString("org_contactNumber"));
                // Map representative fields
                if (hasColumn(rs, "org_repName")) org.setRepName(rs.getString("org_repName"));
                if (hasColumn(rs, "org_repCnic")) org.setRepCnic(rs.getString("org_repCnic"));
                if (hasColumn(rs, "org_repEmail")) org.setRepEmail(rs.getString("org_repEmail"));
                if (hasColumn(rs, "org_repContactNumber")) org.setRepContactNumber(rs.getString("org_repContactNumber"));
                opp.setOrganisation(org);
            }
        } catch (Exception ignore) { }

        return opp;
    }

    // Helper to check if a column exists in the ResultSet metadata
    private static boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static Application mapApplication(ResultSet rs) throws SQLException {
        Application app = new Application();
        app.setApplicationId(rs.getInt("applicationId"));
        app.setApplicationComment(rs.getString("applicationComment"));
        app.setOpportunityId(rs.getInt("opportunityId"));
        app.setVolunteerId(rs.getInt("volunteerId"));

        // Map Volunteer from ResultSet with aliases
        Volunteer volunteer = new Volunteer();
        volunteer.setVolunteerId(rs.getInt("vol_volunteerId"));
        volunteer.setPhone(rs.getString("vol_phone"));
        volunteer.setCnic(rs.getString("vol_cnic"));
        volunteer.setAge(rs.getInt("vol_age"));
        volunteer.setCity(rs.getString("vol_city"));
        volunteer.setAvailability(rs.getInt("vol_availability"));
        volunteer.setRating(rs.getFloat("vol_rating"));
        volunteer.setBio(rs.getString("vol_bio"));
        volunteer.setName(rs.getString("vol_name"));
        volunteer.setEmail(rs.getString("vol_email"));
        volunteer.setStatus(rs.getInt("vol_status"));
        String skills = rs.getString("vol_skills");
        if (skills != null) {
            volunteer.setSkills(java.util.Arrays.asList(skills.split(",")));
        }
        app.setVolunteer(volunteer);

        // Map Opportunity from ResultSet with aliases
        Opportunity opportunity = new Opportunity();
        opportunity.setOpportunityId(rs.getInt("opp_opportunityId"));
        opportunity.setOrganisationId(rs.getInt("opp_organisationId"));
        opportunity.setTitle(rs.getString("opp_title"));
        opportunity.setCategory(rs.getString("opp_category"));
        opportunity.setDescription(rs.getString("opp_description"));
        opportunity.setLocation(rs.getString("opp_location"));

        String s = rs.getString("opp_startDate");
        if (s != null && !s.isEmpty()) {
            try { opportunity.setStartDate(LocalDate.parse(s)); } catch (Exception ignore) { }
        }
        s = rs.getString("opp_endDate");
        if (s != null && !s.isEmpty()) {
            try { opportunity.setEndDate(LocalDate.parse(s)); } catch (Exception ignore) { }
        }
        s = rs.getString("opp_closeDate");
        if (s != null && !s.isEmpty()) {
            try { opportunity.setCloseDate(LocalDate.parse(s)); } catch (Exception ignore) { }
        }
        s = rs.getString("opp_startTime");
        if (s != null && !s.isEmpty()) {
            try {
                opportunity.setStartTime(LocalTime.parse(s));
            } catch (Exception ex) {
                try { opportunity.setStartTime(LocalTime.parse(s, TIME_AMPM)); } catch (Exception ignore) { }
            }
        }
        opportunity.setDuration(rs.getInt("opp_duration"));
        opportunity.setCapacity(rs.getInt("opp_capacity"));
        opportunity.setStatus(rs.getInt("opp_status"));

        String createdAtStr = rs.getString("opp_createdAt");
        if (createdAtStr != null && !createdAtStr.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            opportunity.setCreatedAt(LocalDateTime.parse(createdAtStr, formatter));
        }

        // Map basic organisation info (joined aliases)
        if (hasColumn(rs, "org_organisationId")) {
            Organisation org = new Organisation();
            org.setOrganisationId(rs.getInt("org_organisationId"));
            if (hasColumn(rs, "org_name")) org.setName(rs.getString("org_name"));
            // Map organisation email (from USERS table)
            if (hasColumn(rs, "org_email")) org.setEmail(rs.getString("org_email"));
            if (hasColumn(rs, "org_mission")) org.setMission(rs.getString("org_mission"));
            if (hasColumn(rs, "org_address")) org.setAddress(rs.getString("org_address"));
            if (hasColumn(rs, "org_website")) org.setWebsite(rs.getString("org_website"));
            if (hasColumn(rs, "org_contactNumber")) org.setContactNumber(rs.getString("org_contactNumber"));
            // Map representative fields
            if (hasColumn(rs, "org_repName")) org.setRepName(rs.getString("org_repName"));
            if (hasColumn(rs, "org_repCnic")) org.setRepCnic(rs.getString("org_repCnic"));
            if (hasColumn(rs, "org_repEmail")) org.setRepEmail(rs.getString("org_repEmail"));
            if (hasColumn(rs, "org_repContactNumber")) org.setRepContactNumber(rs.getString("org_repContactNumber"));
            opportunity.setOrganisation(org);
        }
        app.setOpportunity(opportunity);

        // Use createdAt from APPLICATION as applicationDate
        String appCreatedAtStr = rs.getString("applicationCreatedAt");
        if (appCreatedAtStr != null && !appCreatedAtStr.isEmpty()) {
            try {
                app.setApplicationDate(LocalDateTime.parse(appCreatedAtStr, APP_DTF));
            } catch (Exception ignore) { app.setApplicationDate(null); }
        }
        app.setStatus(rs.getInt("applicationStatus"));
        return app;
    }


    public static Volunteer getVolunteerById(int volunteerId) {
        String sql = "SELECT v.*, u.* FROM VOLUNTEER v JOIN USERS u ON v.volunteerId = u.userId WHERE v.volunteerId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, volunteerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Volunteer volunteer = new Volunteer();
                    volunteer.setVolunteerId(rs.getInt("volunteerId"));
                    volunteer.setPhone(rs.getString("phone"));
                    volunteer.setCnic(rs.getString("cnic"));
                    volunteer.setAge(rs.getInt("age"));
                    volunteer.setCity(rs.getString("city"));
                    volunteer.setAvailability(rs.getInt("availability"));
                    volunteer.setRating(rs.getFloat("rating"));
                    volunteer.setName(rs.getString("name"));
                    volunteer.setEmail(rs.getString("email"));
                    volunteer.setStatus(rs.getInt("status"));
                    String skills = rs.getString("skills");
                    if (skills != null) {
                        volunteer.setSkills(java.util.Arrays.asList(skills.split(",")));
                    }
                    return volunteer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Opportunity getOpportunityById(int opportunityId) {
        String sql = "SELECT * FROM OPPORTUNITY WHERE opportunityId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, opportunityId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapOpportunity(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}