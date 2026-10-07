package com.serviceWatch.dto;

import java.time.LocalDateTime;

public class IncidentEventResponseDTO {

    private Long id;
    private Long incidentId;
    private String eventType;
    private String description;

    private Long userId;
    private String userName;
    private String userEmail;
    private String userRole;

    private LocalDateTime createdAt;

    public IncidentEventResponseDTO() {
    }

    public IncidentEventResponseDTO(
            Long id,
            Long incidentId,
            String eventType,
            String description,
            Long userId,
            String userName,
            String userEmail,
            String userRole,
            LocalDateTime createdAt) {

        this.id = id;
        this.incidentId = incidentId;
        this.eventType = eventType;
        this.description = description;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getIncidentId() {
        return incidentId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDescription() {
        return description;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserRole() {
        return userRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}