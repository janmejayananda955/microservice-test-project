package com.authService.service.impl;

import com.authService.dto.LoginRequestDto;
import com.authService.dto.RegisterRequestDto;
import com.authService.entity.RefreshSession;
import com.authService.entity.User;
import com.authService.exception.ApiResponse;
import com.authService.exception.ResourceNotFoundException;
import com.authService.repository.RefreshSessionRepository;
import com.authService.repository.UserRepository;
import com.authService.security.AuthUtil;
import com.authService.security.CustomUserDetails;
import com.authService.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final RefreshSessionRepository refreshSessionRepository;

    @Override
    public ApiResponse<?> register(RegisterRequestDto registerRequestDto) {

        if (registerRequestDto == null) {
            throw new ResourceNotFoundException("Register request cannot be null");
        }
        // if user already exists
        userRepository.findByEmail(registerRequestDto.getEmail())
                .ifPresent(u -> {
                    throw new ResourceNotFoundException("User already exists");
                });
        User user = User.builder()
                .fullName(registerRequestDto.getFullName())
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .build();
        userRepository.save(user);
        return ApiResponse.success(HttpStatus.CREATED.value(),"User registered successfully");
    }

    @Override
    @Transactional
    public ApiResponse<?> login(LoginRequestDto loginRequestDto,
                                HttpServletRequest request, HttpServletResponse response) {

        if(loginRequestDto == null) {
            throw new ResourceNotFoundException("Login request cannot be null");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));
        // check password
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())){
            throw new ResourceNotFoundException("Invalid email or password");
        }

        String accessToken = authUtil.generateAccessToken(customUserDetails);
        String refreshToken = authUtil.generateRefreshToken(customUserDetails);
        // save refresh token in db
        RefreshSession refreshSession = refreshSessionRepository.save(RefreshSession.builder()
                .refreshToken(DigestUtils.sha256Hex(refreshToken))
                .user(user)
                .expiresAt(java.time.LocalDateTime.now().plusDays(7))
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .build()
        );
        // set in cookie
        authUtil.setRefreshTokenInCookie(refreshToken, response);
        return ApiResponse.success(HttpStatus.OK.value(), "Login successful", accessToken);
    }
}

