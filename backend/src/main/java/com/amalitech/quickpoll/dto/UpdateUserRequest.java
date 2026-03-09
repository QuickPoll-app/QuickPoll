package com.amalitech.quickpoll.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {
    public UpdateUserRequest {
        name  = name  != null ? name.trim()  : null;
        email = email != null ? email.trim() : null;
    }
}