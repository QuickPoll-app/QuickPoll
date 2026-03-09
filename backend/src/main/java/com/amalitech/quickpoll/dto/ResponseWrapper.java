package com.amalitech.quickpoll.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public abstract class ResponseWrapper {
    final HttpStatus httpStatus;
    final int code;
    final String message;

    @Getter
    static class Success<D> extends ResponseWrapper {
        final D data;
        Success(HttpStatus httpStatus, int code, String message, D data) {
            super(httpStatus, code, message);
            this.data = data;
        }

    }
    @Getter
    static class Error extends ResponseWrapper {
        Error(HttpStatus httpStatus, int code, String message) {
            super(httpStatus, code, message);
        }
    }

    public static <D> ResponseWrapper success(HttpStatus httpStatus, String message, D data) {
        return new Success<>(httpStatus, httpStatus.value(), message, data);
    }
    public static ResponseWrapper success(HttpStatus httpStatus, String message) {
        return new Success<>(httpStatus, httpStatus.value(), message, null);
    }
    public static ResponseWrapper error(HttpStatus httpStatus, String message) {
        return new Error(httpStatus, httpStatus.value(), message);
    }
}