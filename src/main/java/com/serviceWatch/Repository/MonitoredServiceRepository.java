package com.serviceWatch.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serviceWatch.Entity.MonitoredService;

public interface MonitoredServiceRepository
        extends JpaRepository<MonitoredService, Long> {

}