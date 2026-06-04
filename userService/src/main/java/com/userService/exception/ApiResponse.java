package com.userService.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final int status;
    private final String message;   
    private final T data;
    private final Object errors;
    private final LocalDateTime timestamp;

    private ApiResponse(boolean success, int status, String message, T data, Object errors) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    // for success response(With data)
    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(true, status, message, data, null);
    }

    //for success response(Without data)
    public static <T> ApiResponse<T> success(int status, String message) {
        return new ApiResponse<>(true, status, message, null, null);
    }

    // for error response(with simple)
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(false, status, message, null,null);
    }

    //for error response(with List of errors)
    public static <T> ApiResponse<T> error(int status, String message, Object errors) {
        return new ApiResponse<>(false, status, message, null, errors);
    }
}
