package com.aiurlshortener.dto;

import lombok.Data;

@Data
public class UrlRequest {


    private String originalUrl;

    private String customAlias;

    private Integer expiryDays;
}