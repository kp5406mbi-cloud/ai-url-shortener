package com.aiurlshortener.controller;

import com.aiurlshortener.dto.UrlRequest;
import com.aiurlshortener.repository.UrlRepository;
import com.aiurlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final UrlRepository repository;
    private final UrlService urlService;

    @GetMapping("/")
    public String dashboard(Model model) {

        model.addAttribute(
                "urls",
                repository.findAll());

        model.addAttribute(
                "totalClicks",
                urlService.getTotalClicks());

        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/shorten")
    public String shorten(
            @RequestParam String originalUrl,
            @RequestParam(required = false)
            String customAlias,
            @RequestParam(required = false)
            Integer expiryDays) {

        UrlRequest request = new UrlRequest();

        request.setOriginalUrl(originalUrl);
        request.setCustomAlias(customAlias);
        request.setExpiryDays(expiryDays);

        urlService.shorten(request);

        return "redirect:/";
    }

    @PostMapping("/delete/{code}")
    public String delete(
            @PathVariable String code) {

        urlService.delete(code);

        return "redirect:/";
    }

    @GetMapping("/stats/{code}")
    public String stats(
            @PathVariable String code,
            Model model) {

        model.addAttribute(
                "stats",
                urlService.getStats(code));

        return "stats";
    }


}