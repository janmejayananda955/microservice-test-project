package com.authService.service;

import com.authService.dto.LoginRequestDto;
import com.authService.dto.RegisterRequestDto;
import com.authService.exception.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    ApiResponse<?> register(RegisterRequestDto registerRequestDto);

    ApiResponse<?> login(LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response);
}
