package com.amalitech.quickpoll.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Immutable;

import java.time.LocalDate;

@Entity
@Table(name = "analytics_vote_trends")
@Immutable
@Getter
public class VoteTrend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vote_date")
    private LocalDate voteDate;

    @Column(name = "votes_per_day")
    private Integer votesPerDay;
}