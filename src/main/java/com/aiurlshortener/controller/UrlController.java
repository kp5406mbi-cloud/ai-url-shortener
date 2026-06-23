package com.aiurlshortener.controller;

import com.aiurlshortener.dto.UrlRequest;
import com.aiurlshortener.dto.UrlResponse;
import com.aiurlshortener.dto.UrlStatsResponse;
import com.aiurlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService service;

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shorten(
            @Valid @RequestBody UrlRequest request) {

        return ResponseEntity.ok(service.shorten(request));
    }

    @GetMapping("/stats/{code}")
    public ResponseEntity<UrlStatsResponse> stats(
            @PathVariable String code) {

        return ResponseEntity.ok(
                service.getStats(code));
    }
}