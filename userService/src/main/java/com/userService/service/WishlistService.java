package com.userService.service;

import com.userService.dto.WishlistRequestDto;
import com.userService.exception.ApiResponse;

import java.util.UUID;

public interface WishlistService {
    ApiResponse<?> getWishlist(UUID userId);
    ApiResponse<?> addToWishlist(UUID userId, WishlistRequestDto request);
    ApiResponse<?> removeFromWishlist(UUID userId, String productId);
}
