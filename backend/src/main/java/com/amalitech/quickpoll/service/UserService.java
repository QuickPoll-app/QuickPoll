package com.amalitech.quickpoll.service;

import com.amalitech.quickpoll.dto.ChangePasswordRequest;
import com.amalitech.quickpoll.dto.UpdateUserRequest;
import com.amalitech.quickpoll.dto.UserResponse;
import com.amalitech.quickpoll.exceptionHandler.BadRequestException;
import com.amalitech.quickpoll.exceptionHandler.DuplicateResourceException;
import com.amalitech.quickpoll.exceptionHandler.ResourceNotFoundException;
import com.amalitech.quickpoll.model.User;
import com.amalitech.quickpoll.repository.UserRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Cacheable(cacheNames = "users", key = "'id_' + #userId")
    public UserResponse getById(UUID userId) {
        return UserResponse.fromEntity(
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("User not found with id: " + userId))
        );
    }

    @Cacheable(cacheNames = "users", key = "'email_' + #email")
    public UserResponse getByEmail(String email) {
        return UserResponse.fromEntity(
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("User not found with email: " + email))
        );
    }

    @Cacheable(cacheNames = "users", key = "'page_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponse::fromEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "users", allEntries = true)
    public UserResponse update(UUID userId, UpdateUserRequest userDetails) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + userId));

        userRepository.findByEmail(userDetails.email())
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Email already in use");
                });

        user.setFullName(userDetails.name());
        user.setEmail(userDetails.email());

        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void delete(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + userId));

        userRepository.delete(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void changePassword(UUID userId, ChangePasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}