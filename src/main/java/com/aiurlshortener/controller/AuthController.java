package com.aiurlshortener.controller;

import com.aiurlshortener.dto.LoginRequest;
import com.aiurlshortener.dto.RegisterRequest;
import com.aiurlshortener.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request) {

        service.register(request);

        return ResponseEntity.ok(
                "User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                service.login(request));
    }
}