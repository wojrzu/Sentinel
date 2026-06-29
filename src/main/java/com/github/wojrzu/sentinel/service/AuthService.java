package com.github.wojrzu.sentinel.service;

import com.github.wojrzu.sentinel.dto.AuthResponse;
import com.github.wojrzu.sentinel.dto.LoginRequest;
import com.github.wojrzu.sentinel.dto.RegisterRequest;
import com.github.wojrzu.sentinel.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter
@Setter
public class AuthService {

    private final UserService userService;
    private final TokenService tokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userService.getUserByUsername(loginRequest.getUsername());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getHashedPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isAccountActive()) {
            throw new RuntimeException("Account is disabled");
        }

        return new AuthResponse(tokenService.generateToken(user), user.getUserType().toString());
    }

    public AuthResponse register(RegisterRequest registerRequest, User caller) {
        User user = userService.createUser(registerRequest, caller);
        return new AuthResponse(tokenService.generateToken(user), user.getUserType().toString());
    }

    public AuthResponse refreshAuthToken() {
        return null;
    }
}