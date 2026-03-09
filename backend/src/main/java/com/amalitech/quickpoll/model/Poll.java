package com.amalitech.quickpoll.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "polls")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Poll {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id")
    private User creator;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PollOption> options = new ArrayList<>();

    @Column(name = "multi_select")
    private boolean multiSelect;

    @Builder.Default
    private boolean active = true;

    @Column(name = "expires_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime expiresAt;

    @Column(name = "created_at", updatable = false, insertable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}