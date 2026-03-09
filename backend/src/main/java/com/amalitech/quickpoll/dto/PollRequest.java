package com.amalitech.quickpoll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public record PollRequest (
        @NotBlank(message = "Expected Question")
        String question,
        String description,
        @NotEmpty(message = "Expected Poll Options")
        List<String> options,
        boolean multipleChoice,
        LocalDateTime expiresAt
) {
    public PollRequest {
        question = question.trim();
        description = description.trim();
        options = options.stream().map(String::trim).toList();
    }
}