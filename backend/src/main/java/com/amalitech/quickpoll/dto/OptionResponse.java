package com.amalitech.quickpoll.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;

import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@Builder
public record OptionResponse (
        UUID id,
        String text,
        int voteCount,
        double percentage
) {}