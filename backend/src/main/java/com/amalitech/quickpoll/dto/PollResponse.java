package com.amalitech.quickpoll.dto;

import com.amalitech.quickpoll.model.enums.PollStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PollResponse {
    private UUID id;
    private String question;
    private String description;
    private String creatorName;
    private PollStatus status;
    private boolean multipleChoice;
    private LocalDateTime createdAt;
    private int totalVotes;
    private List<OptionResponse> options;
}
