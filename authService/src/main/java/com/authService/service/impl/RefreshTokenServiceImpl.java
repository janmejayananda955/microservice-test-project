package com.authService.service.impl;

import com.authService.entity.RefreshSession;
import com.authService.entity.User;
import com.authService.exception.ApiResponse;
import com.authService.exception.InvalidCredentialsException;
import com.authService.repository.RefreshSessionRepository;
import com.authService.repository.UserRepository;
import com.authService.security.AuthUtil;
import com.authService.security.CustomUserDetails;
import com.authService.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshSessionRepository refreshSessionRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

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

}
