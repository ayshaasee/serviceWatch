package com.serviceWatch.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.serviceWatch.Entity.User;
import com.serviceWatch.dto.NotificationResponseDTO;
import com.serviceWatch.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponseDTO> getMyNotifications(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return notificationService.getUserNotifications(user);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponseDTO markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return notificationService.markAsRead(id, user);
    }

    @PatchMapping("/read-all")
    public String markAllAsRead(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        notificationService.markAllAsRead(user);

        return "All notifications marked as read";
    }
}