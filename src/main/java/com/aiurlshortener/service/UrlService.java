package com.aiurlshortener.service;

import com.aiurlshortener.dto.UrlRequest;
import com.aiurlshortener.dto.UrlResponse;
import com.aiurlshortener.dto.UrlStatsResponse;
import com.aiurlshortener.entity.Url;
import com.aiurlshortener.exception.BadRequestException;
import com.aiurlshortener.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;



@Service
@RequiredArgsConstructor
public class UrlService {

    @Value("${app.base-url}")
    private String baseUrl;

    private final UrlRepository repository;
    private final UrlCacheService cacheService;

    public UrlResponse shorten(UrlRequest request) {

        String code;

        if (request.getCustomAlias() != null
                && !request.getCustomAlias().isBlank()) {

            String alias = request.getCustomAlias().trim();

            if (!alias.matches("^[a-zA-Z0-9_-]+$")) {
                throw new BadRequestException(
                        "Alias contains invalid characters");
            }

            if (repository.existsByShortCode(alias)) {
                throw new BadRequestException(
                        "Alias already exists");
            }

            code = alias;

        } else {

            do {
                code = UUID.randomUUID()
                        .toString()
                        .substring(0, 7);

            } while (repository.existsByShortCode(code));
        }

        LocalDateTime expiresAt = null;

        if (request.getExpiryDays() != null
                && request.getExpiryDays() > 0) {

            expiresAt = LocalDateTime.now()
                    .plusDays(request.getExpiryDays());
        }

        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode(code)
                .expiresAt(expiresAt)
                .build();

        repository.save(url);

        return UrlResponse.builder()
                .originalUrl(url.getOriginalUrl())
                .shortUrl(baseUrl + "/" + code)
                .build();
    }

    public String getOriginalUrl(String code) {

        System.out.println("Searching for = [" + code + "]");

        Url url = cacheService.getUrlByCode(code);

        System.out.println("Found = [" + url.getShortCode() + "]");

        if (url.getExpiresAt() != null
                && LocalDateTime.now().isAfter(url.getExpiresAt())) {

            throw new BadRequestException(
                    "URL has expired");
        }

        if (url.getClickCount() == null) {
            url.setClickCount(0L);
        }

        url.setClickCount(url.getClickCount() + 1);
        url.setLastAccessedAt(LocalDateTime.now());

        repository.save(url);

        return url.getOriginalUrl();
    }

    public UrlStatsResponse getStats(String code) {

        Url url = repository.findByShortCode(code)
                .orElseThrow(() ->
                        new RuntimeException("URL not found"));

        return UrlStatsResponse.builder()
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .clickCount(url.getClickCount())
                .createdAt(url.getCreatedAt())
                .lastAccessedAt(url.getLastAccessedAt())
                .expiresAt(url.getExpiresAt())
                .build();
    }

    public List<Url> getAllUrls() {
        return repository.findAll();
    }

    public long getTotalClicks() {
        return repository.findAll()
                .stream()
                .mapToLong(url ->
                        url.getClickCount() == null
                                ? 0
                                : url.getClickCount())
                .sum();
    }

    @Transactional
    public void delete(String code) {
        repository.deleteByShortCode(code);
    }

    public long getTotalUrls() {
        return repository.count();
    }

    public long getActiveUrls() {

        return repository.findAll()
                .stream()
                .filter(url ->
                        url.getExpiresAt() == null ||
                                url.getExpiresAt().isAfter(LocalDateTime.now()))
                .count();
    }

    public List<Url> getActiveUrlList() {

        return repository.findAll()
                .stream()
                .filter(url ->
                        url.getExpiresAt() == null ||
                                url.getExpiresAt().isAfter(LocalDateTime.now()))
                .toList();
    }

    public Url getUrlByShortCode(String code) {

        return repository.findByShortCode(code)
                .orElseThrow(() ->
                        new BadRequestException("URL not found"));
    }
}