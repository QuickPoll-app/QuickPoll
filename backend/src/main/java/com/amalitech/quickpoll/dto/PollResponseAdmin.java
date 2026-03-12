package com.amalitech.quickpoll.dto;

import com.amalitech.quickpoll.model.enums.PollStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record PollResponseAdmin(
        UUID id,
        String question,
        String description,
        String creatorName,
        String creatorEmail,
        boolean HasVoted,
        PollStatus status,
        boolean multipleChoice,
        Instant createdAt,
        Instant expiresAt,
        int totalVotes,
        float participationRate,
        List<OptionResponse> options
) {}