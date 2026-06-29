package com.github.wojrzu.sentinel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Officer extends User {
    @Column(nullable = false)
    private int status;

    @ManyToOne(fetch = FetchType.EAGER)
    SuperPower superPower;
}