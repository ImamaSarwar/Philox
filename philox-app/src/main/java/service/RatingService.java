package service;

import db.repository.RatingRepository;
import db.repository.VolunteerRepository;
import db.repository.OrganisationRepository;
import entity.Rating;

import java.util.List;

public class RatingService {

    public static boolean addVolunteerRating(int organisationId, int volunteerId, int rating, String ratingComment) {
        // Validate rating range
        if (rating < 1 || rating > 5) {
            return false;
        }

        // Check if organisation has already rated this volunteer
        if (RatingRepository.hasUserRatedUser(organisationId, volunteerId)) {
            return false; // Already rated
        }

        // Create and save rating
        Rating ratingEntity = new Rating(organisationId, volunteerId, rating, ratingComment);
        boolean saved = RatingRepository.save(ratingEntity);

        if (saved) {
            // Update volunteer's average rating
            double averageRating = RatingRepository.getAverageRating(volunteerId);
            VolunteerRepository.updateRating(volunteerId, averageRating);
        }

        return saved;
    }

    public static boolean addOrganisationRating(int volunteerId, int organisationId, int rating, String ratingComment) {
        // Validate rating range
        if (rating < 1 || rating > 5) {
            return false;
        }

        // Check if volunteer has already rated this organisation
        if (RatingRepository.hasUserRatedUser(volunteerId, organisationId)) {
            return false; // Already rated
        }

        // Create and save rating
        Rating ratingEntity = new Rating(volunteerId, organisationId, rating, ratingComment);
        boolean saved = RatingRepository.save(ratingEntity);

        if (saved) {
            // Update organisation's average rating
            double averageRating = RatingRepository.getAverageRating(organisationId);
            OrganisationRepository.updateRating(organisationId, averageRating);
        }

        return saved;
    }

    public static double getAverageRating(int userId) {
        return RatingRepository.getAverageRating(userId);
    }

    public static List<Rating> getLastNRatings(int userId, int n) {
        return RatingRepository.getLastNRatings(userId, n);
    }

    public static int getRatingCount(int userId) {
        return RatingRepository.getRatingCount(userId);
    }

    public static boolean hasUserRatedUser(int raterId, int rateeId) {
        return RatingRepository.hasUserRatedUser(raterId, rateeId);
    }
}
