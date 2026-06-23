package com.aiurlshortener.service;

import com.aiurlshortener.entity.Url;
import com.aiurlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlCacheService {

    private final UrlRepository repository;

    @Cacheable(value = "urls", key = "#code")
    public Url getUrlByCode(String code) {

        System.out.println("DB HIT: " + code);

        return repository.findByShortCode(code)
                .orElseThrow(() ->
                        new RuntimeException("URL not found"));
    }
}