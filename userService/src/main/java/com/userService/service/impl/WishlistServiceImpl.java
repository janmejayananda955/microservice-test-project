package com.userService.service.impl;

import com.userService.dto.WishlistRequestDto;
import com.userService.entity.UserProfile;
import com.userService.exception.ApiResponse;
import com.userService.exception.ResourceNotFoundException;
import com.userService.repository.UserProfileRepository;
import com.userService.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public ApiResponse<?> getWishlist(UUID userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for userId: " + userId));

        return ApiResponse.success(HttpStatus.OK.value(), "Wishlist retrieved successfully", userProfile.getWishlist());
    }

    @Override
    public ApiResponse<?> addToWishlist(UUID userId, WishlistRequestDto request) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for userId: " + userId));

        if (!userProfile.getWishlist().contains(request.getProductId())) {
            userProfile.getWishlist().add(request.getProductId());
            userProfileRepository.save(userProfile);
        }

        return ApiResponse.success(HttpStatus.OK.value(), "Product added to wishlist successfully", userProfile.getWishlist());
    }

    @Override
    public ApiResponse<?> removeFromWishlist(UUID userId, String productId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for userId: " + userId));

        if (userProfile.getWishlist().contains(productId)) {
            userProfile.getWishlist().remove(productId);
            userProfileRepository.save(userProfile);
        }

        return ApiResponse.success(HttpStatus.OK.value(), "Product removed from wishlist successfully", userProfile.getWishlist());
    }
}
