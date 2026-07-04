package com.aiurlshortener.service;

import com.aiurlshortener.entity.RefreshToken;
import com.aiurlshortener.entity.User;
import com.aiurlshortener.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenDuration;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {

        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Creates a new refresh token.
     */
    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);

        refreshToken.setToken(
                UUID.randomUUID().toString());

        refreshToken.setCreatedAt(
                LocalDateTime.now());

        refreshToken.setExpiryDate(
                LocalDateTime.now()
                        .plusSeconds(refreshTokenDuration));

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Find refresh token.
     */
    public Optional<RefreshToken> findByToken(String token) {

        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Check expiration.
     */
    public RefreshToken verifyExpiration(
            RefreshToken token) {

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {

            refreshTokenRepository.delete(token);

            throw new RuntimeException(
                    "Refresh token expired. Please login again.");
        }

        return token;
    }

    /**
     * Delete refresh token(s) for a user.
     */
    public void deleteByUser(User user) {

        refreshTokenRepository.deleteByUser(user);
    }

    /**
     * Check existence.
     */
    public boolean exists(String token) {

        return refreshTokenRepository.existsByToken(token);
    }
}