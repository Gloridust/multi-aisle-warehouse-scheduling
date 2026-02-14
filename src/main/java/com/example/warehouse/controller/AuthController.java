package com.example.warehouse.controller;

import com.example.warehouse.dto.ChangePasswordRequest;
import com.example.warehouse.dto.LoginRequest;
import com.example.warehouse.dto.LoginResponse;
import com.example.warehouse.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/password")
    public Map<String, Object> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return Map.of("success", true);
    }
}
