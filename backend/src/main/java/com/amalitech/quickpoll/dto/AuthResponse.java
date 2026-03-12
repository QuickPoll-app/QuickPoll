package com.amalitech.quickpoll.dto;

import com.amalitech.quickpoll.model.enums.Role;
import lombok.Builder;

@Builder
public record AuthResponse (
        String token,
        String email,
        String name,
        Role role
) {}