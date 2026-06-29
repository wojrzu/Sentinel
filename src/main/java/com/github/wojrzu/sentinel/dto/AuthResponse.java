package com.github.wojrzu.sentinel.dto;

import com.github.wojrzu.sentinel.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    public String token;
    public String user;
}
