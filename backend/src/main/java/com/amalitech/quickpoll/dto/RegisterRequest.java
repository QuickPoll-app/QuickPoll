package com.amalitech.quickpoll.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
        @NotBlank(message = "Expected Name")
        String name,
        @Email(message = "Expected Email") @NotBlank(message = "Expected Email")
        String email,
        @NotBlank(message = "Expected Password") @Size(min = 6, message = "Password Must Be Greater Than 6 Characters")
        String password
) {
    public RegisterRequest {
        name = name.trim();
        email = email.trim();
    }
}