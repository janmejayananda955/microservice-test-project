package com.userService.service;

import com.userService.dto.AddressRequestDto;
import com.userService.exception.ApiResponse;

import java.util.UUID;

public interface ManageAddressService {
    ApiResponse<?> getAddress(UUID userId);
    ApiResponse<?> updateAddress(UUID userId, AddressRequestDto request);
}
