package com.amalitech.quickpoll.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import java.util.UUID;

public record VoteRequest (
        @NotEmpty(message = "Expected at least one Option ID")
        Set<UUID> optionIds
) {}