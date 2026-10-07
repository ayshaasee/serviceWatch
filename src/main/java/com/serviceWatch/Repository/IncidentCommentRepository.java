package com.serviceWatch.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serviceWatch.Entity.IncidentComment;

public interface IncidentCommentRepository
        extends JpaRepository<IncidentComment, Long> {

    List<IncidentComment> findByIncidentIdOrderByCreatedAtAsc(Long incidentId);
}