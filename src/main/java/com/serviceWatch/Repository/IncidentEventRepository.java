package com.serviceWatch.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serviceWatch.Entity.Incident;
import com.serviceWatch.Entity.IncidentEvent;

public interface IncidentEventRepository extends JpaRepository<IncidentEvent, Long> {

    List<IncidentEvent> findByIncidentOrderByCreatedAtAsc(Incident incident);
}