package com.github.wojrzu.sentinel.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Dispatcher extends User {
    @Column(nullable = false)
    private int status;
}
