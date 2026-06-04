package com.userService.service.impl;

import com.userService.dto.UpdateProfileRequestDto;
import com.userService.dto.UserProfileResponseDto;
import com.userService.entity.UserProfile;
import com.userService.exception.ApiResponse;
import com.userService.exception.ResourceNotFoundException;
import com.userService.repository.UserProfileRepository;
import com.userService.service.UpdateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateProfileServiceImpl implements UpdateProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public ApiResponse<?> updateProfile(UUID userId, UpdateProfileRequestDto request) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for userId: " + userId));

        userProfile.setFullName(request.getFullName());
        userProfile.setPhone(request.getPhone());
        userProfile.setAddress(request.getAddress());
        userProfile.setGender(request.getGender());
        userProfile.setProfilePicture(request.getProfilePicture());

        UserProfile updatedProfile = userProfileRepository.save(userProfile);
        return ApiResponse.success(HttpStatus.OK.value(), "User profile updated successfully", UserProfileResponseDto.fromEntity(updatedProfile));
    }
}
