package com.userService.service;

import com.userService.exception.ApiResponse;

import java.util.UUID;

public interface ViewProfileService {
    ApiResponse<?> viewProfile(UUID userId);
}
