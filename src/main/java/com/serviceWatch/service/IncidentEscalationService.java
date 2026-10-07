package com.serviceWatch.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.serviceWatch.Entity.Incident;
import com.serviceWatch.Entity.MonitoredService;
import com.serviceWatch.Entity.User;
import com.serviceWatch.Repository.IncidentRepository;
import com.serviceWatch.Repository.MonitoredServiceRepository;
import com.serviceWatch.enums.IncidentEventType;
import com.serviceWatch.enums.IncidentSeverity;
import com.serviceWatch.enums.IncidentStatus;
import com.serviceWatch.enums.ServiceHealth;

@Service
public class IncidentEscalationService {
	
	private static final Logger log =
	        LoggerFactory.getLogger(IncidentEscalationService.class);

    private final IncidentRepository incidentRepository;
    private final IncidentEventService incidentEventService;
    private final NotificationService notificationService;
    private final MonitoredServiceRepository monitoredServiceRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public IncidentEscalationService(
            IncidentRepository incidentRepository,
            IncidentEventService incidentEventService,
            NotificationService notificationService,
            MonitoredServiceRepository monitoredServiceRepository) {

        this.incidentRepository = incidentRepository;
        this.incidentEventService = incidentEventService;
        this.notificationService = notificationService;
        this.monitoredServiceRepository = monitoredServiceRepository;
    }

    public void escalateReadyIncidents() {

        List<Incident> incidents = findIncidentsReadyForEscalation();

        for (Incident incident : incidents) {

            incident.setStatus(IncidentStatus.ESCALATED);

            Incident savedIncident = incidentRepository.save(incident);
            log.warn(
                    "Incident automatically escalated: incidentId={}, title={}",
                    savedIncident.getId(),
                    savedIncident.getTitle()
            );

            MonitoredService service = savedIncident.getService();

            ServiceHealth health =
                    calculateServiceHealth(service);

            service.setStatus(health);

            monitoredServiceRepository.save(service);

            User assignedUser = savedIncident.getAssignedTo();

            if (assignedUser != null) {

                notificationService.createNotification(
                        assignedUser,
                        savedIncident,
                        "ESCALATED",
                        "Incident escalated: " + savedIncident.getTitle()
                );
                log.info(
                        "Escalation notification sent: incidentId={}, recipient={}",
                        savedIncident.getId(),
                        assignedUser.getEmail()
                );
            }

            incidentEventService.createEvent(
                    savedIncident,
                    null,
                    IncidentEventType.ESCALATED,
                    "Incident automatically escalated"
            );
        }
    }

    public List<Incident> findIncidentsReadyForEscalation() {

        return incidentRepository
                .findBySeverityAndStatusAndEscalationDeadlineBefore(
                        IncidentSeverity.CRITICAL,
                        IncidentStatus.OPEN,
                        LocalDateTime.now()
                );
    }

    @Scheduled(fixedRate = 60000)
    public void runEscalationCheck() {

        escalateReadyIncidents();
    }

    private ServiceHealth calculateServiceHealth(
            MonitoredService service) {

        // 1. First check whether the actual service is reachable
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

        // 3. Any CRITICAL incident means service is DOWN
        boolean hasCritical = activeIncidents.stream()
                .anyMatch(incident ->
                        incident.getSeverity() == IncidentSeverity.CRITICAL
                );

        if (hasCritical) {
            return ServiceHealth.DOWN;
        }

        // 4. Any HIGH incident means service is DEGRADED
        boolean hasHigh = activeIncidents.stream()
                .anyMatch(incident ->
                        incident.getSeverity() == IncidentSeverity.HIGH
                );

        if (hasHigh) {
            return ServiceHealth.DEGRADED;
        }

        // 5. Service is reachable and has no serious active incidents
        return ServiceHealth.HEALTHY;
    }
}