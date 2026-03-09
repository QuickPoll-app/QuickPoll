package com.amalitech.quickpoll.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OptionResponse {
    private UUID id;
    private String text;
    private int voteCount;
    private double percentage;
}
