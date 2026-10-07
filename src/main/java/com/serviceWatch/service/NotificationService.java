package com.serviceWatch.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.serviceWatch.Entity.Incident;
import com.serviceWatch.Entity.Notification;
import com.serviceWatch.Entity.User;
import com.serviceWatch.Repository.NotificationRepository;
import com.serviceWatch.dto.NotificationResponseDTO;
import com.serviceWatch.exception.NotificationAccessDeniedException;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationResponseDTO createNotification(
            User user,
            Incident incident,
            String type,
            String message) {

        Notification notification = new Notification(
                user,
                incident,
                type,
                message
        );

        Notification savedNotification =
                notificationRepository.save(notification);

        return convertToDTO(savedNotification);
    }

    public List<NotificationResponseDTO> getUserNotifications(User user) {

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    
    public NotificationResponseDTO markAsRead(Long notificationId, User user) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new NotificationAccessDeniedException(
                    "You are not allowed to modify this notification");
        }

        notification.setRead(true);

        Notification savedNotification =
                notificationRepository.save(notification);

        return convertToDTO(savedNotification);
    }
    public void markAllAsRead(User user) {

        List<Notification> notifications =
                notificationRepository.findByUserOrderByCreatedAtDesc(user);

        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
            }
        }

        notificationRepository.saveAll(notifications);
    }
    
    private NotificationResponseDTO convertToDTO(
            Notification notification) {

        return new NotificationResponseDTO(
                notification.getId(),
                notification.getIncident().getId(),
                notification.getType(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}