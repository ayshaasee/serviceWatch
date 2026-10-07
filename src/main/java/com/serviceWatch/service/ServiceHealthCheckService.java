package com.serviceWatch.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.serviceWatch.Entity.Incident;
import com.serviceWatch.Entity.MonitoredService;
import com.serviceWatch.Repository.IncidentRepository;
import com.serviceWatch.Repository.MonitoredServiceRepository;
import com.serviceWatch.enums.IncidentSeverity;
import com.serviceWatch.enums.IncidentStatus;
import com.serviceWatch.enums.ServiceHealth;

@Service
public class ServiceHealthCheckService {

	private final MonitoredServiceRepository serviceRepository;
	private final IncidentRepository incidentRepository;
	private final RestClient restClient;

	public ServiceHealthCheckService(
	        MonitoredServiceRepository serviceRepository,
	        IncidentRepository incidentRepository) {

	    this.serviceRepository = serviceRepository;
	    this.incidentRepository = incidentRepository;
	    this.restClient = RestClient.create();
	}

    public void checkAllServices() {

        List<MonitoredService> services =
                serviceRepository.findAll();

        for (MonitoredService service : services) {

            checkService(service);
        }
    }

    public void checkService(MonitoredService service) {

        if (service.getHealthCheckUrl() == null ||
            service.getHealthCheckUrl().isBlank()) {

            return;
        }

        try {

            Instant start = Instant.now();

            restClient.get()
                    .uri(service.getHealthCheckUrl())
                    .retrieve()
                    .toBodilessEntity();
            
            long responseTime =
                    Duration.between(start, Instant.now())
                            .toMillis();

            service.setResponseTime(responseTime);
            service.setLastChecked(LocalDateTime.now());

            List<Incident> activeIncidents =
                    incidentRepository.findByServiceAndStatusNot(
                            service,
                            IncidentStatus.RESOLVED
                    );

            boolean hasCritical = activeIncidents.stream()
                    .anyMatch(incident ->
                            incident.getSeverity() == IncidentSeverity.CRITICAL
                    );

            boolean hasHigh = activeIncidents.stream()
                    .anyMatch(incident ->
                            incident.getSeverity() == IncidentSeverity.HIGH
                    );
            
            System.out.println(
                    "DEBUG → "
                    + service.getName()
                    + " | Active incidents: "
                    + activeIncidents.size()
                    + " | Has Critical: "
                    + hasCritical
                    + " | Has High: "
                    + hasHigh
            );

            if (hasCritical) {

                service.setStatus(ServiceHealth.DOWN);

            } else if (hasHigh) {

                service.setStatus(ServiceHealth.DEGRADED);

            } else if (responseTime < 1000) {

                service.setStatus(ServiceHealth.HEALTHY);

            } else {

                service.setStatus(ServiceHealth.DEGRADED);
            }

            System.out.println(
                    service.getName()
                    + " → "
                    + service.getStatus()
                    + " ("
                    + responseTime
                    + " ms)"
            );

        } catch (Exception e) {

            service.setStatus(ServiceHealth.DOWN);

            System.out.println(
                    service.getName()
                    + " → DOWN"
            );
            service.setLastChecked(LocalDateTime.now());
            service.setResponseTime(null);
            service.setStatus(ServiceHealth.DOWN);
        }

        serviceRepository.save(service);
    }
    @Scheduled(fixedRate = 30000)
    public void scheduledHealthCheck() {

        checkAllServices();
    }
}