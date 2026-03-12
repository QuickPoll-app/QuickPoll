package com.amalitech.quickpoll.dto;

import com.amalitech.quickpoll.model.enums.PollStatus;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
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
        float participationRate,
        List<OptionResponse> options
) {}