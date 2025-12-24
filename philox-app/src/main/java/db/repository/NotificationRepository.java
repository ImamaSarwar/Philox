package db.repository;

import entity.Notification;
import java.util.List;

public class NotificationRepository {
    public static boolean save(Notification notification) {
        // Insert notification into DB
        return true;
    }

    public static List<Notification> getVolunteerNotifications(int volunteerId) {
        // Query DB for notifications by volunteerId
        return List.of();
    }

    public static List<Notification> getOrganisationNotifications(int organisationId) {
        // Query DB for notifications by organisationId
        return List.of();
    }

    public static List<Notification> getAdminNotifications() {
        // Query DB for admin notifications
        return List.of();
    }

    public static List<Notification> getNotificationsForUser(int userId) {
        // Query DB for notifications by userId
        return List.of();
    }

    public static void markAsRead(int notificationId) {
        // Update notification as read in DB
    }
}
