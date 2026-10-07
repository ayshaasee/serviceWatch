package com.serviceWatch.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceWatch.Entity.Incident;
import com.serviceWatch.dto.IncidentResponseDTO;
import com.serviceWatch.enums.IncidentStatus;
import com.serviceWatch.service.IncidentService;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEAM_LEAD', 'ENGINEER')")
    @PostMapping
    public IncidentResponseDTO createIncident(
            @RequestBody Incident incident) {

        return incidentService.createIncident(incident);
    }

    @GetMapping
    public List<IncidentResponseDTO> getAllIncidents() {

        return incidentService.getAllIncidents();
    }

    @GetMapping("/{id}")
    public IncidentResponseDTO getIncidentById(
            @PathVariable Long id) {

        return incidentService.getIncidentResponseById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEAM_LEAD', 'ENGINEER')")
    @PutMapping("/{id}")
    public IncidentResponseDTO updateIncident(
            @PathVariable Long id,
            @RequestBody Incident updatedIncident) {

        return incidentService.updateIncident(id, updatedIncident);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEAM_LEAD', 'ENGINEER')")
    @PatchMapping("/{id}/status")
    public IncidentResponseDTO updateIncidentStatus(
            @PathVariable Long id,
            @RequestParam IncidentStatus status) {

        return incidentService.updateIncidentStatus(id, status);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEAM_LEAD')")
    @PutMapping("/{incidentId}/assign/{userId}")
    public IncidentResponseDTO assignIncident(
            @PathVariable Long incidentId,
            @PathVariable Long userId) {

        return incidentService.assignIncident(incidentId, userId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEAM_LEAD')")
    @DeleteMapping("/{id}")
    public String deleteIncident(@PathVariable Long id) {

        incidentService.deleteIncident(id);

        return "Incident deleted successfully";
    }
}