package com.authService.controller;

import com.authService.dto.LoginRequestDto;
import com.authService.dto.RegisterRequestDto;
import com.authService.exception.ApiResponse;
import com.authService.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceImpl authService;

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        return authService.register(registerRequestDto);
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto,
                                HttpServletRequest request, HttpServletResponse response) {
        return authService.login(loginRequestDto, request, response);
    }

    @GetMapping("/refresh-token")
    public ApiResponse<?> refresh(@CookieValue(name = "refreshToken", required = false) String incomingRefreshToken,
            HttpServletRequest request, HttpServletResponse response) {
        return authService.refresh(incomingRefreshToken, request, response);
    }

    @GetMapping("/logout")
    public ApiResponse<?> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        return authService.logout(refreshToken, response);
    }

    @GetMapping("/logout-all")
    public ApiResponse<?> logoutAll(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        return authService.logoutAll(refreshToken, response);
    }
}
