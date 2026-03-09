package com.amalitech.quickpoll.controller;

import com.amalitech.quickpoll.dto.*;
import com.amalitech.quickpoll.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user with email, password, and full name")
    public ResponseEntity<ResponseWrapper<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseWrapper.success(HttpStatus.CREATED, "User registered successfully",
                        authService.register(request)));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<ResponseWrapper<AuthResponse>> login(
            @Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(
                ResponseWrapper.success(HttpStatus.OK, "Login successful",
                        authService.login(request)));
    }
}
