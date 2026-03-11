package com.amalitech.quickpoll.dto;

import com.amalitech.quickpoll.model.enums.PollStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record PollResponse (
        UUID id,
        String question,
        String description,
        String creatorName,
        boolean HasVoted,
        PollStatus status,
        boolean multipleChoice,
        Instant createdAt,
        Instant expiresAt,
        int totalVotes,
        int participationRate,
        List<OptionResponse> options
) {}