package com.amalitech.quickpoll.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Immutable;

import java.time.Instant;

@Getter
@Entity
@Table(name = "analytics_poll_summary")
@Immutable
public class PollPerformance {
    @Id
    @Column(name = "poll_id")
    private Long pollId;

    private String title;

    @Column(name = "creator_name")
    private String creatorName;

    @Column(name = "total_votes")
    private Integer totalVotes;

    @Column(name = "created_at")
    private Instant createdAt;
}