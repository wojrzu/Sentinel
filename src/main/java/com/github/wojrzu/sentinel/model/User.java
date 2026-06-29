package com.github.wojrzu.sentinel.model;

import com.github.wojrzu.sentinel.dto.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@NoArgsConstructor
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String hashedPassword;

    @Enumerated(EnumType.STRING)

    @Column(nullable = false)
    private UserType userType;
    @Column(nullable = false)
    private boolean accountActive;
}