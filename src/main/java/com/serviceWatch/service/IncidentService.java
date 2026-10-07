package com.serviceWatch.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.serviceWatch.Entity.Incident;
import com.serviceWatch.Entity.MonitoredService;
import com.serviceWatch.Entity.User;
import com.serviceWatch.Repository.IncidentRepository;
import com.serviceWatch.Repository.MonitoredServiceRepository;
import com.serviceWatch.Repository.UserRepository;
import com.serviceWatch.dto.IncidentResponseDTO;
import com.serviceWatch.enums.IncidentEventType;
import com.serviceWatch.enums.IncidentSeverity;
import com.serviceWatch.enums.IncidentStatus;
import com.serviceWatch.enums.Role;
import com.serviceWatch.enums.ServiceHealth;
import com.serviceWatch.exception.IncidentNotFoundException;
import com.serviceWatch.exception.InvalidIncidentStatusException;
import com.serviceWatch.exception.ServiceNotFoundException;
import com.serviceWatch.exception.ServiceRequiredException;
import com.serviceWatch.exception.UserNotFoundException;

@Service
public class IncidentService {
	private static final Logger log =
	        LoggerFactory.getLogger(IncidentService.class);

    private final IncidentEventService incidentEventService;
    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final MonitoredServiceRepository monitoredServiceRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public IncidentService(
            IncidentRepository incidentRepository,
            UserRepository userRepository,
            IncidentEventService incidentEventService,
            NotificationService notificationService,
            MonitoredServiceRepository monitoredServiceRepository) {

        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
        this.incidentEventService = incidentEventService;
        this.notificationService = notificationService;
        this.monitoredServiceRepository = monitoredServiceRepository;
    }

    public IncidentResponseDTO createIncident(Incident incident) {

        // Load the real service from the database
        if (incident.getService() == null ||
                incident.getService().getId() == null) {

        	throw new ServiceRequiredException("Service is required");
        }

        MonitoredService service =
                monitoredServiceRepository.findById(
                        incident.getService().getId()
                ).orElseThrow(() ->
                        new ServiceNotFoundException(
                                "Service not found: "
                                        + incident.getService().getId()
                        )
                );

        // Attach the real database entity
        incident.setService(service);

        // Set default status
        if (incident.getStatus() == null) {
            incident.setStatus(IncidentStatus.OPEN);
        }

        // Critical incidents get escalation deadline
        if (incident.getSeverity() == IncidentSeverity.CRITICAL) {
            incident.setEscalationDeadline(
                    LocalDateTime.now().plusMinutes(15)
            );
        }

        // Save incident
        Incident savedIncident =
                incidentRepository.save(incident);
        log.info(
                "Incident created: id={}, title={}, severity={}",
                savedIncident.getId(),
                savedIncident.getTitle(),
                savedIncident.getSeverity()
        );

        // Recalculate service health
        ServiceHealth health =
                calculateServiceHealth(service);

        service.setStatus(health);

        monitoredServiceRepository.save(service);

        // Get logged-in user
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Create incident event
        incidentEventService.createEvent(
                savedIncident,
                user,
                IncidentEventType.CREATED,
                "Incident created"
        );

        // Find notification recipients
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        List<User> teamLeads = userRepository.findByRole(Role.TEAM_LEAD);

        admins.addAll(teamLeads);

        // Notify admins and team leads
        for (User recipient : admins) {

            notificationService.createNotification(
                    recipient,
                    savedIncident,
                    "INCIDENT_CREATED",
                    "New incident created: " + savedIncident.getTitle()
            );
        }

        return convertToDTO(savedIncident);
    }

    private IncidentResponseDTO convertToDTO(Incident incident) {

        Long assignedToId = null;
        String assignedToName = null;
        String assignedToEmail = null;
        String assignedToRole = null;

        if (incident.getAssignedTo() != null) {

            assignedToId = incident.getAssignedTo().getId();
            assignedToName = incident.getAssignedTo().getName();
            assignedToEmail = incident.getAssignedTo().getEmail();

            if (incident.getAssignedTo().getRole() != null) {
                assignedToRole = incident.getAssignedTo().getRole().name();
            }
        }

        return new IncidentResponseDTO(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getSeverity().name(),
                incident.getStatus().name(),
                incident.getService().getId(),
                incident.getService().getName(),
                assignedToId,
                assignedToName,
                assignedToEmail,
                assignedToRole,
                incident.getCreatedAt(),
                incident.getResolvedAt()
        );
    }

    public IncidentResponseDTO assignIncident(Long incidentId, Long userId) {

        Incident incident = getIncidentById(incidentId);

        // Person performing the assignment
        User actor = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Person being assigned
        User assignedUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        incident.setAssignedTo(assignedUser);

        Incident savedIncident = incidentRepository.save(incident);
        log.info(
                "Incident assigned: incidentId={}, assignedTo={}",
                savedIncident.getId(),
                assignedUser.getEmail()
        );

        incidentEventService.createEvent(
                savedIncident,
                actor,
                IncidentEventType.ASSIGNED,
                "Incident assigned to " + assignedUser.getName()
        );

        notificationService.createNotification(
                assignedUser,
                savedIncident,
                "ASSIGNED",
                "Incident assigned to you: " + savedIncident.getTitle()
        );

        return convertToDTO(savedIncident);
    }

    public List<IncidentResponseDTO> getAllIncidents() {

        return incidentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Incident getIncidentById(Long id) {

        return incidentRepository.findById(id)
                .orElseThrow(() ->
                        new IncidentNotFoundException(
                                "Incident not found with id: " + id
                        )
                );
    }

    public IncidentResponseDTO getIncidentResponseById(Long id) {

        Incident incident = getIncidentById(id);

        return convertToDTO(incident);
    }

    public IncidentResponseDTO updateIncidentStatus(
            Long id,
            IncidentStatus newStatus) {

        Incident incident = getIncidentById(id);

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        IncidentStatus currentStatus = incident.getStatus();

        boolean validTransition = false;

        if (currentStatus == IncidentStatus.OPEN) {

            validTransition =
                    newStatus == IncidentStatus.ACKNOWLEDGED ||
                    newStatus == IncidentStatus.ESCALATED;

        } else if (currentStatus == IncidentStatus.ACKNOWLEDGED) {

            validTransition =
                    newStatus == IncidentStatus.INVESTIGATING ||
                    newStatus == IncidentStatus.ESCALATED ||
                    newStatus == IncidentStatus.RESOLVED;

        } else if (currentStatus == IncidentStatus.INVESTIGATING) {

            validTransition =
                    newStatus == IncidentStatus.RESOLVED ||
                    newStatus == IncidentStatus.ESCALATED;

        } else if (currentStatus == IncidentStatus.ESCALATED) {

            validTransition =
                    newStatus == IncidentStatus.RESOLVED;
        }

        if (!validTransition) {

            throw new InvalidIncidentStatusException(
                    "Invalid incident status transition: "
                            + currentStatus
                            + " → "
                            + newStatus
            );
        }

        incident.setStatus(newStatus);

        if (newStatus == IncidentStatus.RESOLVED) {
            incident.setResolvedAt(LocalDateTime.now());
        }

        Incident savedIncident = incidentRepository.save(incident);
        log.info(
                "Incident status changed: incidentId={}, from={}, to={}",
                savedIncident.getId(),
                currentStatus,
                newStatus
        );

        MonitoredService service = savedIncident.getService();

        ServiceHealth health = calculateServiceHealth(service);

        service.setStatus(health);

        monitoredServiceRepository.save(service);

        // Create timeline event
        IncidentEventType eventType = IncidentEventType.STATUS_CHANGED;

        if (newStatus == IncidentStatus.RESOLVED) {
            eventType = IncidentEventType.RESOLVED;

        } else if (newStatus == IncidentStatus.ESCALATED) {
            eventType = IncidentEventType.ESCALATED;
        }

        incidentEventService.createEvent(
                savedIncident,
                user,
                eventType,
                "Incident status changed from "
                        + currentStatus
                        + " to "
                        + newStatus
        );

        if (savedIncident.getAssignedTo() != null) {

            String notificationType;
            String notificationMessage;

            if (newStatus == IncidentStatus.ESCALATED) {

                notificationType = "INCIDENT_ESCALATED";

                notificationMessage =
                        "Incident #" + savedIncident.getId()
                                + " has been escalated: "
                                + savedIncident.getTitle();

            } else if (newStatus == IncidentStatus.RESOLVED) {

                notificationType = "INCIDENT_RESOLVED";

                notificationMessage =
                        "Incident #" + savedIncident.getId()
                                + " has been resolved: "
                                + savedIncident.getTitle();

            } else {

                notificationType = "STATUS_CHANGED";

                notificationMessage =
                        "Incident #" + savedIncident.getId()
                                + " status changed from "
                                + currentStatus
                                + " to "
                                + newStatus
                                + ": "
                                + savedIncident.getTitle();
            }

            notificationService.createNotification(
                    savedIncident.getAssignedTo(),
                    savedIncident,
                    notificationType,
                    notificationMessage
            );
        }

        return convertToDTO(savedIncident);
    }

    public IncidentResponseDTO updateIncident(
            Long id,
            Incident updatedIncident) {

        Incident existingIncident = getIncidentById(id);

        existingIncident.setTitle(updatedIncident.getTitle());
        existingIncident.setDescription(updatedIncident.getDescription());
        existingIncident.setSeverity(updatedIncident.getSeverity());

        Incident savedIncident = incidentRepository.save(existingIncident);
        log.info(
                "Incident details updated: incidentId={}",
                savedIncident.getId()
        );
        
        

        // Get logged-in user
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Create timeline event
        incidentEventService.createEvent(
                savedIncident,
                user,
                IncidentEventType.INCIDENT_UPDATED,
                "Incident details updated"
        );

        MonitoredService service = savedIncident.getService();

        if (service != null) {

            ServiceHealth health =
                    calculateServiceHealth(service);

            service.setStatus(health);

            monitoredServiceRepository.save(service);
        }

        return convertToDTO(savedIncident);
    }

    public void deleteIncident(Long id) {

        Incident existingIncident = getIncidentById(id);
        log.info(
                "Incident deleted: incidentId={}",
                existingIncident.getId()
        );

        incidentRepository.delete(existingIncident);
    }

    public ServiceHealth calculateServiceHealth(
            MonitoredService service) {

        // 1. Check whether the actual service is reachable
        String healthCheckUrl = service.getHealthCheckUrl();

        if (healthCheckUrl == null || healthCheckUrl.isBlank()) {
            return ServiceHealth.DOWN;
        }

        try {

            String response = restTemplate.getForObject(
                    healthCheckUrl,
                    String.class
            );

            if (response == null || response.isBlank()) {
                return ServiceHealth.DOWN;
            }

        } catch (Exception e) {

            log.warn(
                    "Health check failed for service: {}",
                    service.getName(),
                    e
            );

            return ServiceHealth.DOWN;
        }

        // 2. Service is reachable, now check active incidents
        List<Incident> activeIncidents =
                incidentRepository.findByServiceAndStatusNot(
                        service,
                        IncidentStatus.RESOLVED
                );

        // 3. CRITICAL incident → DOWN
        boolean hasCritical = activeIncidents.stream()
                .anyMatch(incident ->
                        incident.getSeverity() == IncidentSeverity.CRITICAL
                );

        if (hasCritical) {
            return ServiceHealth.DOWN;
        }

        // 4. HIGH incident → DEGRADED
        boolean hasHigh = activeIncidents.stream()
                .anyMatch(incident ->
                        incident.getSeverity() == IncidentSeverity.HIGH
                );

        if (hasHigh) {
            return ServiceHealth.DEGRADED;
        }

        // 5. Reachable + no serious active incidents → HEALTHY
        return ServiceHealth.HEALTHY;
    }
}