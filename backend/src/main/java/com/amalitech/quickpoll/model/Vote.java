package com.amalitech.quickpoll.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "votes", uniqueConstraints = { @UniqueConstraint(columnNames = {"poll_id", "user_id", "option_id"}) })
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private PollOption option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at", updatable = false, insertable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}