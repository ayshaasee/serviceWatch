package com.serviceWatch.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import com.serviceWatch.Entity.MonitoredService;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.serviceWatch.Entity.Incident;
import com.serviceWatch.Entity.User;
import com.serviceWatch.Repository.IncidentRepository;
import com.serviceWatch.Repository.MonitoredServiceRepository;
import com.serviceWatch.Repository.UserRepository;
import com.serviceWatch.enums.IncidentEventType;
import com.serviceWatch.enums.IncidentSeverity;
import com.serviceWatch.enums.IncidentStatus;
import com.serviceWatch.exception.InvalidIncidentStatusException;

class IncidentServiceTest {
	
	@Mock
	private KafkaProducerService kafkaProducerService;

    private IncidentRepository incidentRepository;
    private UserRepository userRepository;
    private IncidentEventService incidentEventService;
    private NotificationService notificationService;
    private MonitoredServiceRepository monitoredServiceRepository;

    private IncidentService incidentService;

    @BeforeEach
    void setUp() {
    	
        incidentRepository = mock(IncidentRepository.class);
        userRepository = mock(UserRepository.class);
        incidentEventService = mock(IncidentEventService.class);
        notificationService = mock(NotificationService.class);
        monitoredServiceRepository = mock(MonitoredServiceRepository.class);

        incidentService = new IncidentService(
                incidentRepository,
                userRepository,
                incidentEventService,
                notificationService,
                monitoredServiceRepository,
                kafkaProducerService
        );

        User user = new User(
                "Test Engineer",
                "test@servicewatch.com",
                "password123",
                null
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ENGINEER")
                        )
                )
        );

        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRejectInvalidStatusTransition() {

        Incident incident = new Incident();

        incident.setStatus(IncidentStatus.OPEN);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        assertThrows(
                InvalidIncidentStatusException.class,
                () -> incidentService.updateIncidentStatus(
                        1L,
                        IncidentStatus.RESOLVED
                )
        );

        verify(incidentRepository, never()).save(any(Incident.class));
    }
    @Test
    void shouldAllowOpenToAcknowledgedTransition() {

        MonitoredService service = new MonitoredService();

        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.OPEN);
        incident.setSeverity(IncidentSeverity.MEDIUM);
        incident.setService(service);

        // Mock finding the incident
        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        // IMPORTANT:
        // Return the same incident that was passed to save()
        when(incidentRepository.save(any(Incident.class)))
                .thenAnswer(invocation -> {
                    Incident saved = invocation.getArgument(0);
                    return saved;
                });

        // Mock active incidents for health calculation
        when(incidentRepository.findByServiceAndStatusNot(
                eq(service),
                eq(IncidentStatus.RESOLVED)
        )).thenReturn(java.util.Collections.emptyList());

        // Return the same service when saving it
        when(monitoredServiceRepository.save(any(MonitoredService.class)))
                .thenAnswer(invocation -> {
                    MonitoredService saved = invocation.getArgument(0);
                    return saved;
                });

        // Execute
        incidentService.updateIncidentStatus(
                1L,
                IncidentStatus.ACKNOWLEDGED
        );

        // Verify status changed
        assertEquals(
                IncidentStatus.ACKNOWLEDGED,
                incident.getStatus()
        );

        // Verify incident was saved
        verify(incidentRepository).save(incident);

        // Verify service was saved
        verify(monitoredServiceRepository).save(service);

        // Verify timeline event
        verify(incidentEventService).createEvent(
                eq(incident),
                any(User.class),
                eq(IncidentEventType.STATUS_CHANGED),
                anyString()
        );
    }
    @Test
    void shouldResolveInvestigatingIncident() {

        MonitoredService service = new MonitoredService();

        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.INVESTIGATING);
        incident.setSeverity(IncidentSeverity.MEDIUM);
        incident.setService(service);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(incidentRepository.save(any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(incidentRepository.findByServiceAndStatusNot(
                eq(service),
                eq(IncidentStatus.RESOLVED)
        )).thenReturn(java.util.Collections.emptyList());

        when(monitoredServiceRepository.save(any(MonitoredService.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        incidentService.updateIncidentStatus(
                1L,
                IncidentStatus.RESOLVED
        );

        // Verify status changed
        assertEquals(
                IncidentStatus.RESOLVED,
                incident.getStatus()
        );

        // Verify resolvedAt was set
        assertNotNull(incident.getResolvedAt());

        // Verify incident was saved
        verify(incidentRepository).save(incident);

        // Verify service was saved
        verify(monitoredServiceRepository).save(service);

        // Verify RESOLVED timeline event
        verify(incidentEventService).createEvent(
                eq(incident),
                any(User.class),
                eq(IncidentEventType.RESOLVED),
                anyString()
        );
    }
    @Test
    void shouldCreateNotificationWhenIncidentIsAssigned() {

    	MonitoredService service = mock(MonitoredService.class);
    	when(service.getId()).thenReturn(1L);

        Incident incident = new Incident();
        incident.setTitle("Payment API Failure");
        incident.setSeverity(IncidentSeverity.CRITICAL);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setService(service);
        User assignedUser = new User(
                "Test Engineer",
                "engineer@servicewatch.com",
                "password123",
                null
        );

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(assignedUser));

        when(incidentRepository.save(any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        incidentService.assignIncident(1L, 2L);

        assertEquals(assignedUser, incident.getAssignedTo());

        verify(incidentRepository).save(incident);

        verify(notificationService).createNotification(
                eq(assignedUser),
                eq(incident),
                eq("ASSIGNED"),
                eq("Incident assigned to you: Payment API Failure")
        );

        verify(incidentEventService).createEvent(
                eq(incident),
                any(User.class),
                eq(IncidentEventType.ASSIGNED),
                eq("Incident assigned to Test Engineer")
        );
    }
}
