package com.aiurlshortener.controller;

import com.aiurlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final UrlService service;

    @GetMapping("/{code}")
    public void redirect(
            @PathVariable String code,
            HttpServletResponse response)
            throws IOException {

        String originalUrl =
                service.getOriginalUrl(code);

        response.sendRedirect(originalUrl);
    }
}