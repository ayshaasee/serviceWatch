package com.serviceWatch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.serviceWatch.Entity.MonitoredService;
import com.serviceWatch.Repository.MonitoredServiceRepository;

@Service
public class MonitoredServiceService {

    private final MonitoredServiceRepository serviceRepository;

    public MonitoredServiceService(
            MonitoredServiceRepository serviceRepository) {

        this.serviceRepository = serviceRepository;
    }

    public MonitoredService createService(MonitoredService service) {
        return serviceRepository.save(service);
    }

    public List<MonitoredService> getAllServices() {
        return serviceRepository.findAll();
    }

    public MonitoredService getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Service not found with id: " + id
                    )
                );
    }

    public MonitoredService updateService(
            Long id,
            MonitoredService updatedService) {

        MonitoredService existingService =
                getServiceById(id);

        if (updatedService.getName() != null) {
            existingService.setName(updatedService.getName());
        }

        if (updatedService.getDescription() != null) {
            existingService.setDescription(
                    updatedService.getDescription()
            );
        }

        if (updatedService.getOwnerTeam() != null) {
            existingService.setOwnerTeam(
                    updatedService.getOwnerTeam()
            );
        }

        if (updatedService.getStatus() != null) {
            existingService.setStatus(
                    updatedService.getStatus()
            );
        }

        if (updatedService.getHealthCheckUrl() != null) {
            existingService.setHealthCheckUrl(
                    updatedService.getHealthCheckUrl()
            );
        }

        return serviceRepository.save(existingService);
    }

    public void deleteService(Long id) {
        MonitoredService existingService =
                getServiceById(id);

        serviceRepository.delete(existingService);
    }
}