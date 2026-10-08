package com.serviceWatch.service;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

import com.serviceWatch.Entity.MonitoredService;
import com.serviceWatch.Repository.MonitoredServiceRepository;
@Service
public class MonitoredServiceService {

	private final MonitoredServiceRepository serviceRepository;
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	public MonitoredServiceService(
	        MonitoredServiceRepository serviceRepository,
	        StringRedisTemplate redisTemplate,
	        ObjectMapper objectMapper) {

	    this.serviceRepository = serviceRepository;
	    this.redisTemplate = redisTemplate;
	    this.objectMapper = objectMapper;
	}

	public MonitoredService createService(MonitoredService service) {
	    MonitoredService savedService = serviceRepository.save(service);

	    redisTemplate.delete("services:all");

	    return savedService;
	}

    public List<MonitoredService> getAllServices() {

        String cachedData =
                redisTemplate.opsForValue().get("services:all");

        if (cachedData != null) {
            try {
                return objectMapper.readValue(
                        cachedData,
                        objectMapper.getTypeFactory()
                                .constructCollectionType(
                                        List.class,
                                        MonitoredService.class
                                )
                );
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to read services from Redis", e);
            }
        }

        List<MonitoredService> services =
                serviceRepository.findAll();
        try {
            redisTemplate.opsForValue().set(
                    "services:all",
                    objectMapper.writeValueAsString(services)
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to cache services in Redis", e);
        }

        return services;
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

        MonitoredService savedService =
                serviceRepository.save(existingService);

        redisTemplate.delete("services:all");

        return savedService;
    }

    public void deleteService(Long id) {
        MonitoredService existingService =
                getServiceById(id);

        serviceRepository.delete(existingService);

        redisTemplate.delete("services:all");
    }
}