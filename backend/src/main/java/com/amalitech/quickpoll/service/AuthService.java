package com.amalitech.quickpoll.service;

import com.amalitech.quickpoll.dto.*;
import com.amalitech.quickpoll.exceptionHandler.BadRequestException;
import com.amalitech.quickpoll.exceptionHandler.DuplicateResourceException;
import com.amalitech.quickpoll.model.User;
import com.amalitech.quickpoll.model.enums.Role;
import com.amalitech.quickpoll.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimitService rateLimitService;

    private static final String ACTIVE_TOKEN_PREFIX = "active_token:";

    @Transactional(rollbackFor = Exception.class)
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already in use");
        }

        User user = User.builder()
                .fullName(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        storeActiveToken(savedUser.getEmail(), token);
        return buildAuthResponse(savedUser, token);
    }


    public AuthResponse login(AuthRequest request) {

        rateLimitService.checkLoginRateLimit(request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (BadCredentialsException ex) {
            rateLimitService.recordFailedAttempt(request.email());
            long remaining = rateLimitService.getRemainingAttempts(request.email());
            throw new BadCredentialsException(
                    remaining > 0
                            ? "Invalid credentials. " + remaining + " attempts remaining."
                            : "Account temporarily locked. Try again in 15 minutes."
            );
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        rateLimitService.clearFailedAttempts(request.email());

        String previousToken = redisTemplate.opsForValue()
                .get(ACTIVE_TOKEN_PREFIX + user.getEmail());
        if (previousToken != null) {
            jwtService.blacklistToken(previousToken, redisTemplate);
        }

        String token = jwtService.generateToken(user);
        storeActiveToken(user.getEmail(), token);
        return buildAuthResponse(user, token);
    }

    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Invalid authorization header");
        }
        String token = authHeader.substring(7);

        String email = jwtService.extractEmail(token);
        redisTemplate.delete(ACTIVE_TOKEN_PREFIX + email);

        jwtService.blacklistToken(token, redisTemplate);
    }

    private void storeActiveToken(String email, String token) {
        long ttl = jwtService.getExpirationMs();
        redisTemplate.opsForValue().set(
                ACTIVE_TOKEN_PREFIX + email,
                token,
                ttl,
                TimeUnit.MILLISECONDS
        );
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getFullName())
                .role(user.getRole())
                .build();
    }
}