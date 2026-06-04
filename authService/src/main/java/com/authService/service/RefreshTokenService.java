package com.authService.service;

import com.authService.exception.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface RefreshTokenService {
    ApiResponse<?> refresh(String incomingRefreshToken, HttpServletRequest request, HttpServletResponse response);
}
