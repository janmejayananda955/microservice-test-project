package com.authService.service;

import com.authService.dto.LoginRequestDto;
import com.authService.exception.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    ApiResponse<?> login(LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response);
}
