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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
