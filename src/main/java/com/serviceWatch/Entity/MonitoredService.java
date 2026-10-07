package com.serviceWatch.Entity;

import java.time.LocalDateTime;

import com.serviceWatch.enums.ServiceHealth;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "services")
public class MonitoredService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String ownerTeam;

    @Enumerated(EnumType.STRING)
    private ServiceHealth status;

    private LocalDateTime createdAt;
    
    private String healthCheckUrl;
    private Long responseTime;

    private LocalDateTime lastChecked;
    public MonitoredService() {
        this.createdAt = LocalDateTime.now();
    }

    public MonitoredService(
            String name,
            String description,
            String ownerTeam,
            ServiceHealth status) {

        this.name = name;
        this.description = description;
        this.ownerTeam = ownerTeam;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
    
    public String getHealthCheckUrl() {
        return healthCheckUrl;
    }

    public void setHealthCheckUrl(String healthCheckUrl) {
        this.healthCheckUrl = healthCheckUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerTeam() {
        return ownerTeam;
    }

    public void setOwnerTeam(String ownerTeam) {
        this.ownerTeam = ownerTeam;
    }

    public ServiceHealth getStatus() {
        return status;
    }

    public void setStatus(ServiceHealth status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public LocalDateTime getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(LocalDateTime lastChecked) {
        this.lastChecked = lastChecked;
    }
}