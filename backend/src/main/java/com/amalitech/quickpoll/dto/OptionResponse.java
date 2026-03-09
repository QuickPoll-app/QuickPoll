package com.amalitech.quickpoll.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OptionResponse (
        UUID id,
        String text,
        int voteCount,
        double percentage
) {}