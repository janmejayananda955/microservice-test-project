package com.authService.service.impl;

import com.authService.dto.LoginRequestDto;
import com.authService.dto.RegisterRequestDto;
import com.authService.entity.RefreshSession;
import com.authService.entity.User;
import com.authService.exception.ApiResponse;
import com.authService.exception.InvalidCredentialsException;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
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
        return ApiResponse.success(HttpStatus.CREATED.value(), "User registered successfully");
    }

    @Override
    @Transactional
    public ApiResponse<?> login(LoginRequestDto loginRequestDto,
                                HttpServletRequest request, HttpServletResponse response) {

        if (loginRequestDto == null) {
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
        // if user exceeded the refresh token limit
        if (refreshSessionRepository.countActiveByUser(user) >= 3) {
            throw new ResourceNotFoundException("User exceeded the device login limit");
        }
        String accessToken = authUtil.generateAccessToken(customUserDetails);
        String refreshToken = authUtil.generateRefreshToken(customUserDetails);
        // save refresh token in db
        saveRefreshToken(user, refreshToken, request);
        // set in cookie
        authUtil.setRefreshTokenInCookie(refreshToken, response);
        return ApiResponse.success(HttpStatus.OK.value(), "Login successful", accessToken);
    }

    @Override
    @Transactional
    public ApiResponse<?> refresh(String incomingRefreshToken, HttpServletRequest request, HttpServletResponse response) {
        if (incomingRefreshToken == null || incomingRefreshToken.isBlank()) {
            throw new InvalidCredentialsException("Refresh token is required");
        }
        // validate refresh token(by in db present and expired or not)
        RefreshSession refreshSession = refreshSessionRepository
                .findByRefreshToken(DigestUtils.sha256Hex(incomingRefreshToken))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        if (refreshSession.getExpiresAt().isBefore(LocalDateTime.now()) ||
                refreshSession.isRevoked()) {
            // for security detection
            if (refreshSession.isRevoked()) {
                log.warn("DETECTED ATTACK: Attempted reuse of revoked Refresh Token for user: {}", refreshSession.getUser().getEmail());
                //remove all token for that user
                refreshSessionRepository.deleteByUser(refreshSession.getUser());
            }
            throw new InvalidCredentialsException("Session expired or invalid");
        }

        User user = userRepository.findById(refreshSession.getUser().getId())
                .orElseThrow(() -> new InvalidCredentialsException("Unauthorized"));
        // create new access and refresh
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = authUtil.generateAccessToken(userDetails);
        String newRefreshToken = authUtil.generateRefreshToken(userDetails);
        // set new refresh token in db

        refreshSession.setRefreshToken(DigestUtils.sha256Hex(newRefreshToken)); // reset old token
        refreshSession.setExpiresAt(java.time.LocalDateTime.now().plusDays(7)); // reset time
        refreshSessionRepository.save(refreshSession);

        // set in cookie
        authUtil.setRefreshTokenInCookie(newRefreshToken, response);
        return ApiResponse.success(HttpStatus.OK.value(), "Refresh successful", newAccessToken);
    }

    @Override
    public ApiResponse<?> logout(String incomingRefreshToken, HttpServletResponse response) {
        if (incomingRefreshToken == null) {
            throw new InvalidCredentialsException("Unauthorized");
        }
        // verify with both access and refresh token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        RefreshSession session = refreshSessionRepository.findByRefreshToken(DigestUtils.sha256Hex(incomingRefreshToken))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));
        if (!userDetails.getId().equals(session.getUser().getId())) {
            throw new InvalidCredentialsException("Unauthorized");
        }
        session.setRevoked(true);
        refreshSessionRepository.save(session);
        // clear cookie
        authUtil.clearRefreshTokenInCookie(response);
        return ApiResponse.success(HttpStatus.OK.value(), "Logout successful", null);
    }

    @Override
    @Transactional
    public ApiResponse<?> logoutAll(String incomingRefreshToken, HttpServletResponse response) {
        if (incomingRefreshToken == null) {
            throw new InvalidCredentialsException("Unauthorized");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();


        RefreshSession session = refreshSessionRepository.findByRefreshToken(DigestUtils.sha256Hex(incomingRefreshToken))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));
        if (!userDetails.getId().equals(session.getUser().getId())) {
            throw new InvalidCredentialsException("Unauthorized");
        }
        refreshSessionRepository.revokeAllByUserId(userDetails.getId());
        authUtil.clearRefreshTokenInCookie(response);
        return ApiResponse.success(HttpStatus.OK.value(), "Logout all devices successful", null);
    }

    // Helper methods
    private void saveRefreshToken(User user, String refreshToken, HttpServletRequest request) {
        refreshSessionRepository.save(RefreshSession.builder()
                .refreshToken(DigestUtils.sha256Hex(refreshToken))
                .user(user)
                .expiresAt(java.time.LocalDateTime.now().plusDays(7))
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .build()
        );
    }
}


