package com.authService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {

        ApiResponse<?> response = ApiResponse.error(HttpStatus.CONFLICT.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(ResourceNotFoundException ex) {
        int status = HttpStatus.NOT_FOUND.value();

        ApiResponse<?> response = ApiResponse.error(status, ex.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidCredentials(InvalidCredentialsException ex) {
        int status = HttpStatus.UNAUTHORIZED.value();

        ApiResponse<?> response = ApiResponse.error(status, ex.getMessage());
        return ResponseEntity.status(status).body(response);
    }
}
