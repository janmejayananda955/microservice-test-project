package com.userService.service.impl;

import com.userService.dto.CreateUserRequestDto;
import com.userService.dto.UserProfileResponseDto;
import com.userService.entity.UserProfile;
import com.userService.exception.ApiResponse;
import com.userService.exception.ResourceAlreadyExistsException;
import com.userService.repository.UserProfileRepository;
import com.userService.service.CreateUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserImpl implements CreateUser {

    private final UserProfileRepository userProfileRepository;

    @Override
    public ApiResponse<?> createUser(CreateUserRequestDto request) {
        if (userProfileRepository.existsByUserId(request.getUserId())) {
            throw new ResourceAlreadyExistsException("User profile already exists for userId: " + request.getUserId());
        }

        UserProfile userProfile = UserProfile.builder()
                .userId(request.getUserId())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .gender(request.getGender())
                .profilePicture(request.getProfilePicture())
                .build();

        UserProfile savedProfile = userProfileRepository.save(userProfile);
        return ApiResponse.success(HttpStatus.CREATED.value(), "User profile created successfully", UserProfileResponseDto.fromEntity(savedProfile));
    }
}
