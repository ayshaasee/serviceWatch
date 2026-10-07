package com.serviceWatch.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.serviceWatch.Entity.Incident;
import com.serviceWatch.dto.IncidentEventResponseDTO;
import com.serviceWatch.service.IncidentEventService;
import com.serviceWatch.service.IncidentService;

@RestController
@RequestMapping("/api/incidents")
public class IncidentEventController {

    private final IncidentService incidentService;
    private final IncidentEventService incidentEventService;

    public IncidentEventController(
            IncidentService incidentService,
            IncidentEventService incidentEventService) {

        this.incidentService = incidentService;
        this.incidentEventService = incidentEventService;
    }

    @GetMapping("/{incidentId}/timeline")
    public List<IncidentEventResponseDTO> getIncidentTimeline(
            @PathVariable Long incidentId) {

        Incident incident = incidentService.getIncidentById(incidentId);

        return incidentEventService.getEventDTOsForIncident(incident);
    }
}