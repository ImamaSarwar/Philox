package entity;

import java.time.LocalDateTime;

public class Rating {
    private int ratingId;
    private int raterId;
    private int rateeId;
    private int ratingStars;
    private String comment;
    private LocalDateTime createdAt;

    private User rater; // Associated rater User object
    private User ratee; // Associated ratee User object

    public Rating() {}

    public Rating(int raterId, int rateeId, int ratingStars, String comment) {
        this.raterId = raterId;
        this.rateeId = rateeId;
        this.ratingStars = ratingStars;
        this.comment = comment;
    }

    // Getters and Setters
    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public int getRaterId() {
        return raterId;
    }

    public void setRaterId(int raterId) {
        this.raterId = raterId;
    }

    public int getRateeId() {
        return rateeId;
    }

    public void setRateeId(int rateeId) {
        this.rateeId = rateeId;
    }

    public int getRatingStars() {
        return ratingStars;
    }

    public void setRatingStars(int ratingStars) {
        if (ratingStars >= 1 && ratingStars <= 5) {
            this.ratingStars = ratingStars;
        } else {
            throw new IllegalArgumentException("Rating stars must be between 1 and 5");
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getRater() {
        return rater;
    }
    public void setRater(User rater) {
        this.rater = rater;
    }
    public User getRatee() {
        return ratee;
    }
    public void setRatee(User ratee) {
        this.ratee = ratee;
    }
}
