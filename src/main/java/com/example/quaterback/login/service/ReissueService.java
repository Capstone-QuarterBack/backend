package com.example.quaterback.login.service;

import com.example.quaterback.login.entity.RefreshEntity;
import com.example.quaterback.login.jwt.JWTUtil;
import com.example.quaterback.login.repository.refresh.RefreshRepository;
import com.example.quaterback.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ReissueService(JWTUtil jwtUtil,
                          @Qualifier("jpaRefreshRepository") RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    public String validateRefresh(Cookie[] cookies) {
        String refreshToken = CookieUtil.extractRefreshToken(cookies);

        if (!jwtUtil.isValidateRefreshToken(refreshToken)) {

            throw new RuntimeException("invalid token");
        }

        Boolean isExist = refreshRepository.existsByRefresh(refreshToken);
        if (!isExist) {

            throw new RuntimeException("invalid token");
        }

        return refreshToken;
    }

    public String getNewAccessToken(String refreshToken) {
        String username = jwtUtil.getUsername(refreshToken);
        return jwtUtil.createJwt("accessToken", username, 600000L);
    }

    @Transactional
    public String getNewRefreshToken(String refreshToken) {
        String username = jwtUtil.getUsername(refreshToken);
        String newRefresh = jwtUtil.createJwt("refreshToken", username, 86400000L);
        refreshRepository.deleteByRefresh(refreshToken);
        addRefreshEntity(username, newRefresh, 86400000L);
        return newRefresh;
    }

    public void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = RefreshEntity.builder()
                .username(username)
                .refresh(refresh)
                .expiration(date.toString())
                .build();
        refreshRepository.save(refreshEntity);
    }

}
