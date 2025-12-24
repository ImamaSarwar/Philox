package db.repository;

import db.DBConnection;
import entity.Rating;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingRepository {

    public static boolean save(Rating rating) {
        String sql = "INSERT INTO RATING (raterId, rateeId, ratingStars, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rating.getRaterId());
            pstmt.setInt(2, rating.getRateeId());
            pstmt.setInt(3, rating.getRatingStars());
            pstmt.setString(4, rating.getComment());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static double getAverageRating(int rateeId) {
        String sql = "SELECT AVG(CAST(ratingStars AS REAL)) as avgRating FROM RATING WHERE rateeId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rateeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avgRating");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static int getRatingCount(int rateeId) {
        String sql = "SELECT COUNT(*) as count FROM RATING WHERE rateeId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rateeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean hasUserRatedUser(int raterId, int rateeId) {
        String sql = "SELECT COUNT(*) as count FROM RATING WHERE raterId = ? AND rateeId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, raterId);
            pstmt.setInt(2, rateeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Rating> getLastNRatings(int userId, int n) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT r.ratingId, r.raterId, r.rateeId, r.ratingStars, r.comment, r.createdAt, " +
                "rater.userId as rater_userId, rater.name as rater_name, rater.email as rater_email, rater.status as rater_status, " +
                "ratee.userId as ratee_userId, ratee.name as ratee_name, ratee.email as ratee_email, ratee.status as ratee_status " +
                "FROM RATING r " +
                "JOIN USERS rater ON r.raterId = rater.userId " +
                "JOIN USERS ratee ON r.rateeId = ratee.userId " +
                "WHERE r.rateeId = ? " +
                "ORDER BY r.createdAt DESC " +
                "LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, n);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Rating rating = new Rating();
                    rating.setRatingId(rs.getInt("ratingId"));
                    rating.setRaterId(rs.getInt("raterId"));
                    rating.setRateeId(rs.getInt("rateeId"));
                    rating.setRatingStars(rs.getInt("ratingStars"));
                    rating.setComment(rs.getString("comment"));

                    // Set created date if available
                    try {
                        rating.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                    } catch (SQLException e) {
                        // createdAt might be null, ignore
                    }

                    // Create and populate rater User object
                    entity.User rater = new entity.User();
                    rater.setUserId(rs.getInt("rater_userId"));
                    rater.setName(rs.getString("rater_name"));
                    rater.setEmail(rs.getString("rater_email"));
                    rater.setStatus(rs.getInt("rater_status"));
                    rating.setRater(rater);

                    // Create and populate ratee User object
                    entity.User ratee = new entity.User();
                    ratee.setUserId(rs.getInt("ratee_userId"));
                    ratee.setName(rs.getString("ratee_name"));
                    ratee.setEmail(rs.getString("ratee_email"));
                    ratee.setStatus(rs.getInt("ratee_status"));
                    rating.setRatee(ratee);

                    ratings.add(rating);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }
}
