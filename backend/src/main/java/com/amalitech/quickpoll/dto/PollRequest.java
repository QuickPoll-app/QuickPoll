package com.amalitech.quickpoll.dto;

import com.amalitech.quickpoll.exceptionHandler.BadRequestException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record PollRequest (
        @NotBlank(message = "Expected Question")
        String question,
        @NotBlank(message = "Expected Description")
        String description,
        @NotEmpty(message = "Expected Poll Options")
        List<String> options,
        @NotNull(message = "Expected Multiple Choice or Single Choice")
        boolean multipleChoice,
        @NotNull(message = "Expected Expiry Date")
        Instant expiresAt
) {
    public PollRequest {
        question = question.trim();
        description = description.trim();
        options = options.stream().map(String::trim).distinct().toList();
        options.forEach(o -> {
            if (o.isBlank()) throw new BadRequestException("Poll option text cannot be empty");
        });
        if (options.size() < 2) throw new BadRequestException("Poll must have at least two options");
        if (expiresAt.isAfter(Instant.now())) throw new BadRequestException("Poll expiry date cannot be in the future");
    }
}