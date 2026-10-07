package com.serviceWatch.controller;

import java.util.List;

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

    @PutMapping("/{id}")
    public MonitoredService updateService(
            @PathVariable Long id,
            @RequestBody MonitoredService updatedService) {

        return service.updateService(id, updatedService);
    }

    @DeleteMapping("/{id}")
    public String deleteService(@PathVariable Long id) {

        service.deleteService(id);

        return "Service deleted successfully";
    }
}