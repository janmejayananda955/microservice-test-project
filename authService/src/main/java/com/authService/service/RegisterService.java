package com.authService.service;

import com.authService.dto.RegisterRequestDto;
import com.authService.exception.ApiResponse;

public interface RegisterService {
    ApiResponse<?> register(RegisterRequestDto registerRequestDto);
}
