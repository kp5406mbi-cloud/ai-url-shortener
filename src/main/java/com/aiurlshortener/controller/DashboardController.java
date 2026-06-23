package com.aiurlshortener.controller;

import com.aiurlshortener.dto.UrlRequest;
import com.aiurlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UrlService urlService;

    @PostMapping("/dashboard/shorten")
    public String shortenUrl(
            @ModelAttribute UrlRequest request
    ) {

        urlService.shorten(request);

        return "redirect:/";
    }
}