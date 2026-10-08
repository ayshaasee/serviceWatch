package com.serviceWatch.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.serviceWatch.Entity.MonitoredService;
import com.serviceWatch.service.MonitoredServiceService;

@RestController
@RequestMapping("/api/services")
public class MonitoredServiceController {

    private final MonitoredServiceService service;

    public MonitoredServiceController(
            MonitoredServiceService service) {
        this.service = service;
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAM_LEAD')")
    @PostMapping
    public MonitoredService createService(
            @RequestBody MonitoredService service) {

        return this.service.createService(service);
    }

    @GetMapping
    public List<MonitoredService> getAllServices() {
        return service.getAllServices();
    }

    @GetMapping("/{id}")
    public MonitoredService getServiceById(
            @PathVariable Long id) {

        return service.getServiceById(id);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAM_LEAD')")
    @PutMapping("/{id}")
    public MonitoredService updateService(
            @PathVariable Long id,
            @RequestBody MonitoredService updatedService) {

        return service.updateService(id, updatedService);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAM_LEAD')")
    @DeleteMapping("/{id}")
    public String deleteService(@PathVariable Long id) {

        service.deleteService(id);

        return "Service deleted successfully";
    }
}