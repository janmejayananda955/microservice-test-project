package com.userService.exception;

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

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        int status = HttpStatus.BAD_REQUEST.value();
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiResponse<?> response = ApiResponse.error(status, "Validation failed", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(
            org.springframework.http.converter.HttpMessageNotReadableException ex) {
        int status = HttpStatus.BAD_REQUEST.value();
        String message = "Malformed JSON request or invalid input value";

        Throwable mostSpecificCause = ex.getMostSpecificCause();
        if (mostSpecificCause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException) {
            com.fasterxml.jackson.databind.exc.InvalidFormatException ife = (com.fasterxml.jackson.databind.exc.InvalidFormatException) mostSpecificCause;
            if (ife.getTargetType().isEnum()) {
                message = "Invalid Role Type";
            }
        } else if (mostSpecificCause != null) {
            message = mostSpecificCause.getMessage();
        }

        ApiResponse<?> response = ApiResponse.error(status, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
