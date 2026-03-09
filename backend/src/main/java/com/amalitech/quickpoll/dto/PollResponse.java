package com.amalitech.quickpoll.dto;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record PollResponse (
        UUID id,
        String question,
        String description,
        String creatorName,
        boolean HasVoted,
        String status,
        boolean multipleChoice,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        int totalVotes,
        List<OptionResponse> options
) {}