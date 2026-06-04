package com.authService.security;

import com.authService.exception.ResourceNotFoundException;
import com.authService.repository.UserRepository;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilterChain extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("request coming from {}", request.getRequestURI());

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.split(" ")[1];
            String username = authUtil.getUsernameFromToken(token);
            String role = authUtil.getRoleFromToken(token);

            if (role != null && !role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                CustomUserDetails userDetails = userRepository.findByEmail(username)
                        .map(CustomUserDetails::new)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                List<GrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority(role));
//                UsernamePasswordAuthenticationToken authenticationToken =
//                        new UsernamePasswordAuthenticationToken(
//                                userDetails, null, userDetails.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                //---------OR----------------
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
            }
            filterChain.doFilter(request, response);
        } catch (SignatureException e) {
            sendErrorResponse(response, "Invalid JWT signature", e);
        } catch (Exception e) {
            sendErrorResponse(response, "Invalid token", e);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, Exception e) throws IOException {
        log.warn("JWT filter error: {}", message);
        log.error("JWT filter error: {}", e.getMessage());
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}");
    }
}
