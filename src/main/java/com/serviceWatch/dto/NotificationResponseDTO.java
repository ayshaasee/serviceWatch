package com.serviceWatch.dto;

import java.time.LocalDateTime;

public class NotificationResponseDTO {

    private Long id;
    private Long incidentId;
    private String type;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationResponseDTO() {
    }

    public NotificationResponseDTO(
            Long id,
            Long incidentId,
            String type,
            String message,
            boolean read,
            LocalDateTime createdAt) {

        this.id = id;
        this.incidentId = incidentId;
        this.type = type;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getIncidentId() {
        return incidentId;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}