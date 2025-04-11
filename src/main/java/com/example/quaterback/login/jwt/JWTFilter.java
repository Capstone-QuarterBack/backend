package com.example.quaterback.login.jwt;

import com.example.quaterback.login.dto.CustomUserDetails;
import com.example.quaterback.login.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private static final List<String> EXCLUDE_URIS = List.of(
            "/login",
            "/join"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return EXCLUDE_URIS.contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if (!jwtUtil.hasAuthorizationToken(authorization)) {
            filterChain.doFilter(request, response);

            return;
        }

        String accessToken = authorization.substring(7);

        if (!jwtUtil.isValidateAccessToken(accessToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        String username = jwtUtil.getUsername(accessToken);

        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .build();
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
