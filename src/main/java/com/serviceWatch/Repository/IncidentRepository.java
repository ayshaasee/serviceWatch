package com.serviceWatch.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serviceWatch.Entity.Incident;
import com.serviceWatch.Entity.MonitoredService;
import com.serviceWatch.enums.IncidentSeverity;
import com.serviceWatch.enums.IncidentStatus;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
	
	List<Incident> findBySeverityAndStatusAndEscalationDeadlineBefore(
	        IncidentSeverity severity,
	        IncidentStatus status,
	        LocalDateTime time
	);
	
	List<Incident> findByServiceAndStatusNot(
	        MonitoredService service,
	        IncidentStatus status
	);

}