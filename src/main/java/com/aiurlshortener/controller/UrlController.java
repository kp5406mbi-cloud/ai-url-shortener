package com.aiurlshortener.controller;

import com.aiurlshortener.dto.UrlRequest;
import com.aiurlshortener.dto.UrlResponse;
import com.aiurlshortener.dto.UrlStatsResponse;
import com.aiurlshortener.entity.Url;
import com.aiurlshortener.service.QRCodeService;
import com.aiurlshortener.service.UrlService;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService service;
    private final QRCodeService qrCodeService;

    @Value("${app.base-url}")
    private String baseUrl;

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shorten(
            @Valid @RequestBody UrlRequest request) {

        return ResponseEntity.ok(service.shorten(request));
    }

    @GetMapping(value = "/{code}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCode(
            @PathVariable String code)
            throws WriterException, IOException {

        Url url = service.getUrlByShortCode(code);

        String shortUrl = baseUrl + "/" + url.getShortCode();

        byte[] qr = qrCodeService.generateQRCode(shortUrl);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qr);
    }

    @GetMapping("/stats/{code}")
    public ResponseEntity<UrlStatsResponse> stats(
            @PathVariable String code) {

        return ResponseEntity.ok(service.getStats(code));
    }

    @GetMapping("/config")
    public String config() {
        return baseUrl;
    }
}