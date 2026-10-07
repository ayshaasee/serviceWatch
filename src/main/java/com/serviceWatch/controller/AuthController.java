package com.serviceWatch.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.serviceWatch.Entity.User;
import com.serviceWatch.dto.LoginRequestDTO;
import com.serviceWatch.dto.LoginResponseDTO;
import com.serviceWatch.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }
}