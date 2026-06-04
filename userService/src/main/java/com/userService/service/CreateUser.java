package com.userService.service;

import com.userService.dto.CreateUserRequestDto;
import com.userService.exception.ApiResponse;

public interface CreateUser {
    ApiResponse<?> createUser(CreateUserRequestDto request);
}
