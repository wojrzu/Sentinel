package com.github.wojrzu.sentinel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SuperPower {
    @Id
    @Enumerated(EnumType.STRING)
    private PowerType name;

    public enum PowerType{
        FLIGHT,
        STRENGTH,
        ICE,
        SPEED,
        TELEPATHY
    }
}
