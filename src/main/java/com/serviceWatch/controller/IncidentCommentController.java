package com.serviceWatch.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.serviceWatch.Entity.IncidentComment;
import com.serviceWatch.Entity.User;
import com.serviceWatch.dto.IncidentCommentRequestDTO;
import com.serviceWatch.dto.IncidentCommentResponseDTO;
import com.serviceWatch.service.IncidentCommentService;

@RestController
@RequestMapping("/api/incidents")
public class IncidentCommentController {

    private final IncidentCommentService commentService;

    public IncidentCommentController(
            IncidentCommentService commentService) {

        this.commentService = commentService;
    }

    @PostMapping("/{incidentId}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAM_LEAD', 'ENGINEER')")
    public IncidentCommentResponseDTO addComment(

            @PathVariable Long incidentId,

            @RequestBody IncidentCommentRequestDTO request,

            @AuthenticationPrincipal User currentUser) {

        return commentService.addComment(
                incidentId,
                currentUser,
                request.getComment()
        );
    }

    @GetMapping("/{incidentId}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAM_LEAD', 'ENGINEER')")
    public List<IncidentCommentResponseDTO> getComments(
            @PathVariable Long incidentId) {

        return commentService.getComments(incidentId);
    }
}