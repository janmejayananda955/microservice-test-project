package com.authService.service.impl;

import com.authService.dto.RegisterRequestDto;
import com.authService.entity.User;
import com.authService.entity.enums.Role;
import com.authService.exception.ApiResponse;
import com.authService.exception.ResourceNotFoundException;
import com.authService.repository.UserRepository;
import com.authService.service.RegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<?> register(RegisterRequestDto registerRequestDto) {

        if (registerRequestDto == null) {
            throw new ResourceNotFoundException("Register request cannot be null");
        }
        // check if role is admin
        if (registerRequestDto.getRole().equals(Role.ADMIN)) {
            throw new ResourceNotFoundException("Admin role is not allowed");
        }
        // if user already exists
        userRepository.findByEmail(registerRequestDto.getEmail())
                .ifPresent(u -> {
                    throw new ResourceNotFoundException("User already exists");
                });
        User user = User.builder()
                .role(registerRequestDto.getRole())
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .build();
        userRepository.save(user);
        return ApiResponse.success(HttpStatus.CREATED.value(), "User registered successfully");
    }
}
