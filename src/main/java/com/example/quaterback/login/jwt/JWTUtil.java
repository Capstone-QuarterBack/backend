package com.example.quaterback.login.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    public String getCategory(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("category", String.class);
    }

    public Boolean isValidate(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .after(new Date());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT Token", e);
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT Token", e);
        } catch (SignatureException e) {
            log.error("invalid Signature", e);
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.error("JWT security error", e);
        } catch (JwtException e) {
            log.error("JWT Exception", e);
        }
        return false;
    }

    public boolean hasAuthorizationToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer "))
            return false;
        try {
            String accessToken = authorization.substring(7);
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    public boolean isValidateAccessToken(String token) {
        return isValidate(token) && getCategory(token).equals("accessToken");
    }

    public boolean isValidateRefreshToken(String token) {
        return token != null && isValidate(token) && getCategory(token).equals("refreshToken");
    }

    public String createJwt(String category, String username, Long expiredMs) {

        return Jwts.builder()
                .claim("jti", UUID.randomUUID().toString())
                .claim("category", category)
                .claim("username", username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

}
