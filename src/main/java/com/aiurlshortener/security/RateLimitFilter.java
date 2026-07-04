package com.aiurlshortener.security;

import com.aiurlshortener.dto.RateLimitErrorResponse;
import com.aiurlshortener.service.RateLimiterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class RateLimitFilter extends OncePerRequestFilter {



    private final ObjectMapper objectMapper;
    private final RateLimiterService rateLimiterService;

    public RateLimitFilter(
            RateLimiterService rateLimiterService,
            ObjectMapper objectMapper) {

        this.rateLimiterService = rateLimiterService;
        this.objectMapper = objectMapper;
    }




    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        /*
         * Skip rate limiting for Swagger,
         * API docs and static resources.
         */
        if (path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/favicon.ico")
                || path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/images")) {

            filterChain.doFilter(request, response);
            return;
        }

        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isBlank()) {
            ipAddress = request.getRemoteAddr();
        } else {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        boolean allowed = rateLimiterService.allowRequest(ipAddress);

        if (!allowed) {

            long retryAfter = rateLimiterService.getRetryAfter(ipAddress);

            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            response.setHeader(
                    "Retry-After",
                    String.valueOf(retryAfter));

            response.setHeader(
                    "X-RateLimit-Limit",
                    String.valueOf(rateLimiterService.getMaxRequests()));

            response.setHeader(
                    "X-RateLimit-Remaining",
                    String.valueOf(rateLimiterService.getRemainingRequests(ipAddress)));

            RateLimitErrorResponse errorResponse =
                    new RateLimitErrorResponse(
                            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            429,
                            "Too Many Requests",
                            "Rate limit exceeded. Please try again later.",
                            retryAfter
                    );

            response.getWriter().write(
                    objectMapper.writeValueAsString(errorResponse)
            );

            return;
        }

        filterChain.doFilter(request, response);
    }


}