package com.serviceWatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.serviceWatch.Entity.MonitoredService;
import com.serviceWatch.Entity.Incident;
import com.serviceWatch.Entity.User;
import com.serviceWatch.Repository.IncidentRepository;
import com.serviceWatch.Repository.MonitoredServiceRepository;
import com.serviceWatch.Repository.UserRepository;
import com.serviceWatch.enums.IncidentEventType;
import com.serviceWatch.enums.IncidentSeverity;
import com.serviceWatch.enums.IncidentStatus;
import com.serviceWatch.enums.ServiceHealth;

class IncidentEscalationServiceTest {

    private IncidentRepository incidentRepository;
    private IncidentEventService incidentEventService;
    private UserRepository userRepository;
    private NotificationService notificationService;

    private IncidentEscalationService escalationService;
    private MonitoredServiceRepository monitoredServiceRepository;

    @BeforeEach
    void setUp() {

        incidentRepository = mock(IncidentRepository.class);

        incidentEventService = mock(IncidentEventService.class);

        notificationService = mock(NotificationService.class);

        monitoredServiceRepository = mock(MonitoredServiceRepository.class);

        escalationService = new IncidentEscalationService(
                incidentRepository,
                incidentEventService,
                notificationService,
                monitoredServiceRepository
        );
    }

    @Test
    void shouldAutomaticallyEscalateCriticalIncident() {

        User assignedUser =
                new User("Test Engineer", "engineer@test.com", "password123", null);

        MonitoredService service = new MonitoredService();

        Incident incident = new Incident();

        incident.setStatus(IncidentStatus.OPEN);
        incident.setSeverity(IncidentSeverity.CRITICAL);
        incident.setTitle("Payment Service Failure");
        incident.setAssignedTo(assignedUser);
        incident.setService(service);
        
        when(incidentRepository.findByServiceAndStatusNot(
                eq(service),
                eq(IncidentStatus.RESOLVED)
        )).thenReturn(List.of(incident));

        when(incidentRepository
                .findBySeverityAndStatusAndEscalationDeadlineBefore(
                        eq(IncidentSeverity.CRITICAL),
                        eq(IncidentStatus.OPEN),
                        any()))
                .thenReturn(List.of(incident));

        when(incidentRepository.save(any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(monitoredServiceRepository
                .findById(anyLong()))
                .thenReturn(java.util.Optional.of(service));

        escalationService.escalateReadyIncidents();

        assertEquals(IncidentStatus.ESCALATED, incident.getStatus());
        assertEquals(ServiceHealth.DOWN, service.getStatus());
        verify(monitoredServiceRepository).save(service);

        verify(incidentRepository).save(incident);

        verify(notificationService).createNotification(
                eq(assignedUser),
                eq(incident),
                eq("ESCALATED"),
                eq("Incident escalated: Payment Service Failure")
        );

        verify(incidentEventService).createEvent(
                eq(incident),
                eq(null),
                eq(IncidentEventType.ESCALATED),
                anyString()
        );
    }
}