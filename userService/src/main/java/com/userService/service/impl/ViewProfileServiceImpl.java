package com.userService.service.impl;

import com.userService.dto.UserProfileResponseDto;
import com.userService.entity.UserProfile;
import com.userService.exception.ApiResponse;
import com.userService.exception.ResourceNotFoundException;
import com.userService.repository.UserProfileRepository;
import com.userService.service.ViewProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ViewProfileServiceImpl implements ViewProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public ApiResponse<?> viewProfile(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for userId: " + userId));

        return ApiResponse.success(HttpStatus.OK.value(), "User profile retrieved successfully", UserProfileResponseDto.fromEntity(userProfile));
    }
}
