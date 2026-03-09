package com.amalitech.quickpoll.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public abstract class ResponseWrapper<D> {
    final HttpStatus httpStatus;
    final int code;
    final String message;

    @Getter
    static class Success<D> extends ResponseWrapper<D> {
        final D data;
        Success(HttpStatus httpStatus, int code, String message, D data) {
            super(httpStatus, code, message);
            this.data = data;
        }

    }
    @Getter
    static class Error<D> extends ResponseWrapper<D> {
        Error(HttpStatus httpStatus, int code, String message) {
            super(httpStatus, code, message);
        }
    }

    public static <D> ResponseWrapper<D> success(HttpStatus httpStatus, String message, D data) {
        return new Success<>(httpStatus, httpStatus.value(), message, data);
    }
    public static <D> ResponseWrapper<D> success(HttpStatus httpStatus, String message) {
        return new Success<>(httpStatus, httpStatus.value(), message, null);
    }
    public static <D> ResponseWrapper<D> error(HttpStatus httpStatus, String message) {
        return new Error<>(httpStatus, httpStatus.value(), message);
    }
}