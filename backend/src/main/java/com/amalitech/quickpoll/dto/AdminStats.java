package com.amalitech.quickpoll.dto;

import lombok.Builder;

@Builder
public record AdminStats (
        long totalPolls,
        long totalVotes,
        long totalUsers,
        float participationRate
) {}
