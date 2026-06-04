package com.authService.service;

import com.authService.exception.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface LogoutService {
    ApiResponse<?> logout(String refreshToken, HttpServletResponse response);

    ApiResponse<?> logoutAll(String refreshToken, HttpServletResponse response);
}
