package com.serviceWatch.dto;

import java.time.LocalDateTime;

public class IncidentResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String severity;
    private String status;

    private Long serviceId;
    private String serviceName;

    private Long assignedToId;
    private String assignedToName;
    private String assignedToEmail;
    private String assignedToRole;

    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public IncidentResponseDTO() {
    }

    public IncidentResponseDTO(
            Long id,
            String title,
            String description,
            String severity,
            String status,
            Long serviceId,
            String serviceName,
            Long assignedToId,
            String assignedToName,
            String assignedToEmail,
            String assignedToRole,
            LocalDateTime createdAt,
            LocalDateTime resolvedAt) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.assignedToId = assignedToId;
        this.assignedToName = assignedToName;
        this.assignedToEmail = assignedToEmail;
        this.assignedToRole = assignedToRole;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSeverity() {
        return severity;
    }

    public String getStatus() {
        return status;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Long getAssignedToId() {
        return assignedToId;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public String getAssignedToEmail() {
        return assignedToEmail;
    }

    public String getAssignedToRole() {
        return assignedToRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
}