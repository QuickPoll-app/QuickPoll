package com.amalitech.quickpoll.exceptionHandler;

import com.amalitech.quickpoll.dto.ResponseWrapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream().map(f -> f.getField() + ": " + f.getDefaultMessage()).collect(Collectors.joining("; "));
        if (msg.isEmpty()) msg = "Validation failed";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.error(HttpStatus.BAD_REQUEST, msg));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).collect(Collectors.joining("; "));
        if (msg.isEmpty()) msg = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.error(HttpStatus.BAD_REQUEST, msg));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.error(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.error(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseWrapper.error(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.error(HttpStatus.BAD_REQUEST, "Invalid request body: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Void>> handleAny(Exception ex) {
        log.error("Unhandled exception caught: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseWrapper.error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.error(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleDuplicateResource(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseWrapper.error(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.error(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleAccessDenied(
            AuthorizationDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ResponseWrapper.error(HttpStatus.FORBIDDEN, "Access denied"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleAuthentication(
            AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseWrapper.error(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleTooManyRequests(TooManyRequestsException ex) {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ResponseWrapper.error(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage()));
    }
}