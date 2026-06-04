package com.userService.service;

import com.userService.dto.UpdateProfileRequestDto;
import com.userService.exception.ApiResponse;

import java.util.UUID;

public interface UpdateProfileService {
    ApiResponse<?> updateProfile(UUID userId, UpdateProfileRequestDto request);
}
