package com.serviceWatch.dto;

import java.time.LocalDateTime;

public class IncidentCommentResponseDTO {

    private Long id;
    private Long incidentId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userRole;
    private String comment;
    private LocalDateTime createdAt;

    public IncidentCommentResponseDTO() {
    }

    public IncidentCommentResponseDTO(
            Long id,
            Long incidentId,
            Long userId,
            String userName,
            String userEmail,
            String userRole,
            String comment,
            LocalDateTime createdAt) {

        this.id = id;
        this.incidentId = incidentId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getIncidentId() {
        return incidentId;
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

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}