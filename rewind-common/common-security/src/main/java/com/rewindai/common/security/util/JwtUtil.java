package com.rewindai.common.security.util;

import com.rewindai.common.security.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具类
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public static final String CLAIM_KEY_TYPE = "type";
    public static final String TYPE_ADMIN = "admin";
    public static final String TYPE_APP = "app";

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成Token - 通用方法（Long ID）
     */
    private String generateToken(Long userId, String username, String type) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim(CLAIM_KEY_TYPE, type)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成Token - 通用方法（UUID ID）
     */
    private String generateToken(String userId, String username, String type) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .claim(CLAIM_KEY_TYPE, type)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成App用户Token（UUID）
     */
    public String generateAppToken(String userId, String username) {
        return generateToken(userId, username, TYPE_APP);
    }

    /**
     * 生成Admin用户Token（String ID）
     */
    public String generateAdminToken(String userId, String username) {
        return generateToken(userId, username, TYPE_ADMIN);
    }

    /**
     * 生成Admin用户Token（Long）
     */
    public String generateAdminToken(Long userId, String username) {
        return generateToken(userId.toString(), username, TYPE_ADMIN);
    }

    /**
     * 生成Admin用户Token（Integer）
     */
    public String generateAdminToken(Integer userId, String username) {
        return generateToken(userId.toString(), username, TYPE_ADMIN);
    }

    /**
     * 从Token中获取用户ID（Long）
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从Token中获取用户ID（String，用于UUID）
     */
    public String getUserIdAsStringFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * 从Token中获取用户类型
     */
    public String getUserTypeFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get(CLAIM_KEY_TYPE, String.class);
    }

    /**
     * 解析Claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("无效的JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims为空: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 获取过期时间
     */
    public Long getExpirationInSeconds() {
        return jwtProperties.getExpiration() / 1000;
    }
}
