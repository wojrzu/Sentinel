package com.github.wojrzu.sentinel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    Long reportId;

    @Column
    String title;

    @Column
    String description;

    @Column
    LocalTime reportArrivalTime;

    @Column
    LocalTime reportClosureTime;

    @Column
    UUID reporter;

    @Column
    UUID handlerID;

    @Column
    String address;

    @OneToOne(fetch = FetchType.EAGER)
    Officer assignedOfficer;

    @Column
    ReportState state;

    @ManyToMany(fetch = FetchType.EAGER)
    List<Tag> tags;

    public enum ReportState{
        NEW,
        PENDING,
        ACTIVE,
        FINISHED,
    }
}
