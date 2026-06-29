package com.github.wojrzu.sentinel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag {
    @Id
    @Enumerated(EnumType.STRING)
    private reportType name;

    public enum reportType {
        ROBBERY,
        FIRE,
        CRASH,
        SHOOTING
    }
}
