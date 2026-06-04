package com.userService.service;

import com.userService.exception.ApiResponse;

import java.util.UUID;

public interface CustomerDetailsService {
    ApiResponse<?> getCustomerDetails(UUID userId);
}
