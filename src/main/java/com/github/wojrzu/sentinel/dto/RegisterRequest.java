package com.github.wojrzu.sentinel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {
    @NotNull
    String username;
    @NotNull
    String password;
    @NotNull
    UserType userType;
    String email;
    String firstName;
    String lastName;
}
