package com.userService.service.impl;

import com.userService.dto.AddressRequestDto;
import com.userService.entity.UserProfile;
import com.userService.exception.ApiResponse;
import com.userService.exception.ResourceNotFoundException;
import com.userService.repository.UserProfileRepository;
import com.userService.service.ManageAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageAddressServiceImpl implements ManageAddressService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public ApiResponse<?> getAddress(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for userId: " + userId));

        return ApiResponse.success(HttpStatus.OK.value(), "Address retrieved successfully", userProfile.getAddress());
    }

    @Override
    public ApiResponse<?> updateAddress(UUID userId, AddressRequestDto request) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for userId: " + userId));

        userProfile.setAddress(request.getAddress());
        userProfileRepository.save(userProfile);

        return ApiResponse.success(HttpStatus.OK.value(), "Address updated successfully", userProfile.getAddress());
    }
}
