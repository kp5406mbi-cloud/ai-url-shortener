package com.aiurlshortener.service;

import com.aiurlshortener.dto.LoginRequest;
import com.aiurlshortener.dto.RegisterRequest;
import com.aiurlshortener.entity.User;
import com.aiurlshortener.exception.BadRequestException;
import com.aiurlshortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {

        if(repository.existsByEmail(
                request.getEmail())) {

            throw new BadRequestException(
                    "Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(
                        encoder.encode(
                                request.getPassword()))
                .build();

        repository.save(user);
    }

    public String login(LoginRequest request) {

        User user = repository.findByEmail(
                        request.getEmail())
                .orElseThrow(() ->
                        new BadRequestException(
                                "Invalid credentials"));


        if(!encoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new BadRequestException(
                    "Invalid credentials");
        }

        return jwtService.generateToken(
                user.getEmail());
    }


}