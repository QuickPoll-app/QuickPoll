package com.amalitech.quickpoll.dto;

import java.time.Instant;

public record PollPerformanceDto(
        Long poll_id,
        String title,
        String creator_name,
        Integer total_votes,
        Instant created_at
) {}