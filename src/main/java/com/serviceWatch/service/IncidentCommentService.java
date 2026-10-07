package com.serviceWatch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.serviceWatch.Entity.Incident;
import com.serviceWatch.Entity.IncidentComment;
import com.serviceWatch.Entity.User;
import com.serviceWatch.Repository.IncidentCommentRepository;
import com.serviceWatch.Repository.IncidentRepository;
import com.serviceWatch.dto.IncidentCommentResponseDTO;

@Service
public class IncidentCommentService {

    private final IncidentCommentRepository commentRepository;
    private final IncidentRepository incidentRepository;

    public IncidentCommentService(
            IncidentCommentRepository commentRepository,
            IncidentRepository incidentRepository) {

        this.commentRepository = commentRepository;
        this.incidentRepository = incidentRepository;
    }

    public IncidentCommentResponseDTO addComment(
            Long incidentId,
            User user,
            String comment) {

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() ->
                        new RuntimeException("Incident not found"));

        IncidentComment incidentComment =
                new IncidentComment(
                        incident,
                        user,
                        comment
                );

        IncidentComment saved =
                commentRepository.save(incidentComment);

        return convertToDTO(saved);
    }

    public List<IncidentCommentResponseDTO> getComments(
            Long incidentId) {

        return commentRepository
                .findByIncidentIdOrderByCreatedAtAsc(incidentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private IncidentCommentResponseDTO convertToDTO(
            IncidentComment comment) {

        return new IncidentCommentResponseDTO(
                comment.getId(),
                comment.getIncident().getId(),
                comment.getUser().getId(),
                comment.getUser().getName(),
                comment.getUser().getEmail(),
                comment.getUser().getRole().name(),
                comment.getComment(),
                comment.getCreatedAt()
        );
    }
}