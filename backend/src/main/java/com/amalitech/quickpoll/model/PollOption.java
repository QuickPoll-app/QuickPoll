package com.amalitech.quickpoll.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "poll_options")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PollOption {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    private Poll poll;

    @Column(name = "option_text", nullable = false)
    private String optionText;
}