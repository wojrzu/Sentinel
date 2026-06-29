package com.github.wojrzu.sentinel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {
    public String username;
    public String email;
    public String password;
}
