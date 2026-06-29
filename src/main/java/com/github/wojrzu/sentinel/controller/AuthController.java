package com.github.wojrzu.sentinel.controller;

import com.github.wojrzu.sentinel.dto.AuthResponse;
import com.github.wojrzu.sentinel.dto.LoginRequest;
import com.github.wojrzu.sentinel.dto.RegisterRequest;
import com.github.wojrzu.sentinel.dto.UserType;
import com.github.wojrzu.sentinel.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        request.setUserType(UserType.CLIENT);
        return ResponseEntity.ok(authService.register(request, null));
    }
}