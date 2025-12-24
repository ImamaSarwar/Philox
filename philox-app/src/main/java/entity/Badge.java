package entity;

import java.time.LocalDateTime;

public class Badge {
    private int badgeId;
    private String badgeName;
    private int badgeCriteria;
    private String description;
    private String iconPath;

    // Criteria fields from BADGE_CRITERIA table
    private int participationCount;
    private int applicationCount;
    private double ratingThreshold;

    LocalDateTime awardedAt; // Optional field to track when the badge was awarded

    public Badge() {}

    public Badge(int badgeId, String badgeName, String description, String iconPath, int participationCount,
                 int applicationCount, double ratingThreshold) {
        this.badgeId = badgeId;
        this.badgeName = badgeName;
        this.description = description;
        this.iconPath = iconPath;
        this.participationCount = participationCount;
        this.applicationCount = applicationCount;
        this.ratingThreshold = ratingThreshold;
    }

    public Badge(String badgeName, String description, String iconPath,
                 int participationCount, int applicationCount, double ratingThreshold) {
        this.badgeName = badgeName;
        this.description = description;
        this.iconPath = iconPath;
        this.participationCount = participationCount;
        this.applicationCount = applicationCount;
        this.ratingThreshold = ratingThreshold;
    }

    // Getters and Setters
    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public int getBadgeCriteria() {
        return badgeCriteria;
    }

    public void setBadgeCriteria(int badgeCriteria) {
        this.badgeCriteria = badgeCriteria;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public int getParticipationCount() { return participationCount;}

    public void setParticipationCount(int participationCount) { this.participationCount = participationCount;}

    public int getApplicationCount() { return applicationCount; }

    public void setApplicationCount(int applicationCount) { this.applicationCount = applicationCount; }

    public double getRatingThreshold() { return ratingThreshold; }

    public void setRatingThreshold(double ratingThreshold) { this.ratingThreshold = ratingThreshold; }

    public LocalDateTime getAwardedAt() {
        return awardedAt;
    }
    public void setAwardedAt(LocalDateTime awardedAt) {
        this.awardedAt = awardedAt;
    }
}
