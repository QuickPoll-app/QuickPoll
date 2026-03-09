package com.amalitech.quickpoll.dto;

import com.amalitech.quickpoll.model.User;
import com.amalitech.quickpoll.model.enums.Role;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
Role role,
        Instant createdAt,
Instant updatedAt

) implements Serializable {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

}
