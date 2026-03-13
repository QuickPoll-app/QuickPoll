package com.amalitech.quickpoll.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Immutable;

@Entity
@Table(name = "analytics_user_participation")
@Immutable
@Getter
public class UserParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voter_name")
    private String voterName;

    @Column(name = "total_votes_cast")
    private Integer totalVotesCast;

    @Column(name = "participation_rate")
    private Double participationRate;
}