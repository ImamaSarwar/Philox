package db.repository;

import db.DBConnection;
import entity.Badge;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BadgeRepository {


    public static List<Badge> getVolunteerBadges(int volunteerId) {
        List<Badge> badges = new ArrayList<>();
        String sql = "SELECT b.badgeId, b.badgeName, b.badgeCriteria, b.description, b.iconPath, vb.awardedAt " +
                     "FROM VOLUNTEER_BADGE vb " +
                     "JOIN BADGE b ON vb.badgeId = b.badgeId " +
                     "WHERE vb.volunteerId = ? " +
                     "ORDER BY vb.awardedAt DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, volunteerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Badge badge = new Badge();
                    badge.setBadgeId(rs.getInt("badgeId"));
                    badge.setBadgeName(rs.getString("badgeName"));
                    badge.setBadgeCriteria(rs.getInt("badgeCriteria"));
                    badge.setDescription(rs.getString("description"));
                    badge.setIconPath(rs.getString("iconPath"));

                    // Parse awardedAt timestamp
                    String awardedAtStr = rs.getString("awardedAt");
                    if (awardedAtStr != null) {
                        try {
                            // Try different timestamp formats
                            LocalDateTime awardedAt;
                            if (awardedAtStr.contains("T")) {
                                awardedAt = LocalDateTime.parse(awardedAtStr);
                            } else if (awardedAtStr.contains("-")) {
                                awardedAt = LocalDateTime.parse(awardedAtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                            } else {
                                awardedAt = LocalDateTime.parse(awardedAtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            }
                            badge.setAwardedAt(awardedAt);
                        } catch (Exception e) {
                            // If parsing fails, set to null
                            badge.setAwardedAt(null);
                        }
                    }

                    badges.add(badge);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return badges;
    }


    public static List<Badge> getAllBadges() {
        List<Badge> badges = new ArrayList<>();
        String sql = "SELECT b.badgeId, b.badgeName, b.badgeCriteria, b.description, b.iconPath, " +
                     "b.participationCount, b.applicationCount, b.ratingThreshold " +
                     "FROM BADGE b " +
                     "ORDER BY b.badgeId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Badge badge = new Badge();
                badge.setBadgeId(rs.getInt("badgeId"));
                badge.setBadgeName(rs.getString("badgeName"));
                badge.setBadgeCriteria(rs.getInt("badgeCriteria"));
                badge.setDescription(rs.getString("description"));
                badge.setIconPath(rs.getString("iconPath"));

                // Set criteria fields
                badge.setParticipationCount(rs.getInt("participationCount"));
                badge.setApplicationCount(rs.getInt("applicationCount"));
                badge.setRatingThreshold(rs.getDouble("ratingThreshold"));

                badges.add(badge);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return badges;
    }

    public static boolean createBadge(Badge badge) {
        String insertBadgeSql = "INSERT INTO BADGE (badgeName, badgeCriteria, description, iconPath, participationCount, applicationCount, ratingThreshold) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertBadgeSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false); // Start transaction

            pstmt.setString(1, badge.getBadgeName());
            pstmt.setInt(2, badge.getBadgeCriteria());
            pstmt.setString(3, badge.getDescription());
            pstmt.setString(4, badge.getIconPath());
            pstmt.setInt(5, badge.getParticipationCount());
            pstmt.setInt(6, badge.getApplicationCount());
            pstmt.setDouble(7, badge.getRatingThreshold());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Get the generated badge ID using SQLite's last_insert_rowid()
                String getIdSql = "SELECT last_insert_rowid()";
                try (PreparedStatement idStmt = conn.prepareStatement(getIdSql);
                     ResultSet rs = idStmt.executeQuery()) {
                    if (rs.next()) {
                        int badgeId = rs.getInt(1);
                        badge.setBadgeId(badgeId);

                        // Check for eligible volunteers and award the badge
                        awardBadgeToEligibleVolunteers(conn, badgeId, badge);

                        conn.commit();
                        return true;
                    }
                }
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void awardBadgeToEligibleVolunteers(Connection conn, int badgeId, Badge badge) throws SQLException {
        // Get all volunteers
        String getVolunteersSql = "SELECT userId FROM USERS WHERE type = 2 AND status = 1";

        try (PreparedStatement getVolunteersStmt = conn.prepareStatement(getVolunteersSql);
             ResultSet volunteersRs = getVolunteersStmt.executeQuery()) {

            while (volunteersRs.next()) {
                int volunteerId = volunteersRs.getInt("userId");

                if (isVolunteerEligibleForBadge(conn, volunteerId, badge)) {
                    // Award the badge
                    String awardBadgeSql = "INSERT OR IGNORE INTO VOLUNTEER_BADGE (volunteerId, badgeId) VALUES (?, ?)";
                    try (PreparedStatement awardStmt = conn.prepareStatement(awardBadgeSql)) {
                        awardStmt.setInt(1, volunteerId);
                        awardStmt.setInt(2, badgeId);
                        awardStmt.executeUpdate();
                    }
                }
            }
        }
    }

    private static boolean isVolunteerEligibleForBadge(Connection conn, int volunteerId, Badge badge) throws SQLException {
        // Check participation count (completed applications)
        if (badge.getParticipationCount() > 0) {
            String participationSql = "SELECT COUNT(*) as count FROM APPLICATION WHERE volunteerId = ? AND status = 1";
            try (PreparedStatement pstmt = conn.prepareStatement(participationSql)) {
                pstmt.setInt(1, volunteerId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt("count") < badge.getParticipationCount()) {
                        return false;
                    }
                }
            }
        }

        // Check application count (total applications)
        if (badge.getApplicationCount() > 0) {
            String applicationSql = "SELECT COUNT(*) as count FROM APPLICATION WHERE volunteerId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(applicationSql)) {
                pstmt.setInt(1, volunteerId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt("count") < badge.getApplicationCount()) {
                        return false;
                    }
                }
            }
        }

        // Check rating threshold (average rating as rateeId)
        if (badge.getRatingThreshold() > 0) {
            String ratingSql = "SELECT AVG(ratingValue) as avgRating FROM RATING WHERE rateeId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(ratingSql)) {
                pstmt.setInt(1, volunteerId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        double avgRating = rs.getDouble("avgRating");
                        if (rs.wasNull() || avgRating < badge.getRatingThreshold()) {
                            return false;
                        }
                    } else {
                        return false; // No ratings found
                    }
                }
            }
        }

        return true;
    }

    public static boolean deleteBadge(int badgeId) {
        String deleteSql = "DELETE FROM BADGE WHERE badgeId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {

            pstmt.setInt(1, badgeId);
            int affectedRows = pstmt.executeUpdate();

            // The VOLUNTEER_BADGE entries will be automatically deleted due to CASCADE
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void updateBadgeIconPath(int badgeId, String destinationPath) {
        String updateSql = "UPDATE BADGE SET iconPath = ? WHERE badgeId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

            pstmt.setString(1, destinationPath);
            pstmt.setInt(2, badgeId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
