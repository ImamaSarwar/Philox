package service;

import entity.Notification;
import db.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;

public class NotificationService {
    public static void sendNotification(int userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);
        NotificationRepository.save(notification);
    }

    public static List<Notification> getUserNotifications(int userId) {
        return NotificationRepository.getNotificationsForUser(userId);
    }

    public static void markNotificationAsRead(int notificationId) {
        NotificationRepository.markAsRead(notificationId);
    }
}
