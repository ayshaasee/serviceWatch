package com.serviceWatch.service;

import java.util.List;
import com.serviceWatch.Entity.User;

import org.springframework.stereotype.Service;

import com.serviceWatch.Entity.Incident;
import com.serviceWatch.Entity.IncidentEvent;
import com.serviceWatch.Repository.IncidentEventRepository;
import com.serviceWatch.dto.IncidentEventResponseDTO;
import com.serviceWatch.enums.IncidentEventType;

@Service
public class IncidentEventService {
	
	private IncidentEventResponseDTO convertToDTO(IncidentEvent event) {

	    Long userId = null;
	    String userName = null;
	    String userEmail = null;
	    String userRole = null;

	    if (event.getUser() != null) {
	        userId = event.getUser().getId();
	        userName = event.getUser().getName();
	        userEmail = event.getUser().getEmail();

	        if (event.getUser().getRole() != null) {
	            userRole = event.getUser().getRole().name();
	        }
	    }

	    return new IncidentEventResponseDTO(
	            event.getId(),
	            event.getIncident().getId(),
	            event.getEventType().name(),
	            event.getDescription(),
	            userId,
	            userName,
	            userEmail,
	            userRole,
	            event.getCreatedAt()
	    );
	}

    private final IncidentEventRepository incidentEventRepository;

    public IncidentEventService(IncidentEventRepository incidentEventRepository) {
        this.incidentEventRepository = incidentEventRepository;
    }

    public IncidentEvent createEvent(
            Incident incident,
            User user,
            IncidentEventType eventType,
            String description) {

        IncidentEvent event = new IncidentEvent(
                incident,
                eventType,
                description
        );

        event.setUser(user);

        return incidentEventRepository.save(event);
    }

    public List<IncidentEvent> getEventsForIncident(Incident incident) {

        return incidentEventRepository
                .findByIncidentOrderByCreatedAtAsc(incident);
    }
    
    public List<IncidentEventResponseDTO> getEventDTOsForIncident(Incident incident) {

        return incidentEventRepository
                .findByIncidentOrderByCreatedAtAsc(incident)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
}