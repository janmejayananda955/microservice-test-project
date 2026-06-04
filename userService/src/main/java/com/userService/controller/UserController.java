package com.userService.controller;

import com.userService.dto.AddressRequestDto;
import com.userService.dto.CreateUserRequestDto;
import com.userService.dto.UpdateProfileRequestDto;
import com.userService.dto.WishlistRequestDto;
import com.userService.exception.ApiResponse;
import com.userService.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final CreateUser createUserService;
    private final UpdateProfileService updateProfileService;
    private final ViewProfileService viewProfileService;
    private final ManageAddressService manageAddressService;
    private final WishlistService wishlistService;
    private final CustomerDetailsService customerDetailsService;

    // Create user
    @PostMapping
    public ApiResponse<?> createUser(@Valid @RequestBody CreateUserRequestDto request) {
        return createUserService.createUser(request);
    }

    // Update Profile
    @PutMapping("/{userId}")
    public ApiResponse<?> updateProfile(@PathVariable UUID userId, @Valid @RequestBody UpdateProfileRequestDto request) {
        return updateProfileService.updateProfile(userId, request);
    }

    // View Profile
    @GetMapping("/{userId}")
    public ApiResponse<?> viewProfile(@PathVariable UUID userId) {
        return viewProfileService.viewProfile(userId);
    }

    // Manage Address
    @GetMapping("/{userId}/address")
    public ApiResponse<?> getAddress(@PathVariable UUID userId) {
        return manageAddressService.getAddress(userId);
    }

    @PutMapping("/{userId}/address")
    public ApiResponse<?> updateAddress(@PathVariable UUID userId, @Valid @RequestBody AddressRequestDto request) {
        return manageAddressService.updateAddress(userId, request);
    }

    // Wishlist
    @GetMapping("/{userId}/wishlist")
    public ApiResponse<?> getWishlist(@PathVariable UUID userId) {
        return wishlistService.getWishlist(userId);
    }

    @PostMapping("/{userId}/wishlist")
    public ApiResponse<?> addToWishlist(@PathVariable UUID userId, @Valid @RequestBody WishlistRequestDto request) {
        return wishlistService.addToWishlist(userId, request);
    }

    @DeleteMapping("/{userId}/wishlist/{productId}")
    public ApiResponse<?> removeFromWishlist(@PathVariable UUID userId, @PathVariable String productId) {
        return wishlistService.removeFromWishlist(userId, productId);
    }

    // Customer Details
    @GetMapping("/{userId}/details")
    public ApiResponse<?> getCustomerDetails(@PathVariable UUID userId) {
        return customerDetailsService.getCustomerDetails(userId);
    }
}
