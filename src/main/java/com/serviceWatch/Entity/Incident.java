package com.serviceWatch.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.Index;

import com.serviceWatch.enums.IncidentSeverity;
import com.serviceWatch.enums.IncidentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import com.serviceWatch.Entity.User;

@Entity
@Table(
    name = "incidents",
    indexes = {
        @Index(
            name = "idx_incident_escalation",
            columnList = "severity, status, escalation_deadline"
        ),
        @Index(
            name = "idx_incident_service",
            columnList = "service_id"
        )
    }
)
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private IncidentSeverity severity;

    @Enumerated(EnumType.STRING)
    private IncidentStatus status;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private MonitoredService service;
    
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;
    
    private LocalDateTime escalationDeadline;
    
    public LocalDateTime getEscalationDeadline() {
        return escalationDeadline;
    }
    
    public void setEscalationDeadline(LocalDateTime escalationDeadline) {
        this.escalationDeadline = escalationDeadline;
    }

    public Incident() {
        this.createdAt = LocalDateTime.now();
    }

    public Incident(
            String title,
            String description,
            IncidentSeverity severity,
            IncidentStatus status,
            MonitoredService service) {

        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.service = service;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(IncidentSeverity severity) {
        this.severity = severity;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public MonitoredService getService() {
        return service;
    }

    public void setService(MonitoredService service) {
        this.service = service;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }
}