package com.github.wojrzu.sentinel.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Client extends User {
    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private int ownedPlan;

    @Column(nullable = false)
    private boolean subscriptionActive;

    private String firstName;

    private String lastName;
}
