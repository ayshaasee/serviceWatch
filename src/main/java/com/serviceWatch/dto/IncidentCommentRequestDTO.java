package com.serviceWatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class IncidentCommentRequestDTO {

    @NotBlank(message = "Comment is required")
    @Size(max = 2000, message = "Comment cannot exceed 2000 characters")
    private String comment;

    public IncidentCommentRequestDTO() {
    }

    public IncidentCommentRequestDTO(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}