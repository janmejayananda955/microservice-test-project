package com.authService.service.impl;

import com.authService.entity.RefreshSession;
import com.authService.exception.ApiResponse;
import com.authService.exception.InvalidCredentialsException;
import com.authService.repository.RefreshSessionRepository;
import com.authService.security.AuthUtil;
import com.authService.security.CustomUserDetails;
import com.authService.service.LogoutService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {
    private final RefreshSessionRepository refreshSessionRepository;
    private final AuthUtil authUtil;

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

}
