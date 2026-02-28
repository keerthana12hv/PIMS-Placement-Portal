package com.pims.controller.auth;

import com.pims.dto.request.LoginRequest;
import com.pims.dto.request.RegisterRequest;
import com.pims.dto.response.AuthResponse;
import com.pims.service.auth.AuthService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid@RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}
