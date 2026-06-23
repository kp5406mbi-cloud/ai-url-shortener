package com.aiurlshortener.repository;

import com.aiurlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    @Transactional
    void deleteByShortCode(String shortCode);
}