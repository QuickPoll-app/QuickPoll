package com.amalitech.quickpoll.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest (
        @Email(message = "Expected Email") @NotBlank(message = "Expected Email")
        String email,
        @NotBlank(message = "Expected Password")
        String password
) {
    public AuthRequest {
        email = email.trim();
    }
}