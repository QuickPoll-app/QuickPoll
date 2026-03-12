package com.amalitech.quickpoll.controller;

import com.amalitech.quickpoll.dto.ResponseWrapper;
import com.amalitech.quickpoll.dto.VoteRequest;
import com.amalitech.quickpoll.model.User;
import com.amalitech.quickpoll.service.PollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
@Tag(name = "Votes", description = "Vote management endpoint")
public class VoteController {
    private final PollService pollService;

    @PostMapping("/{id}/vote")
    @Operation(summary = "Record a vote for a poll")
    public ResponseEntity<ResponseWrapper<Void>> vote(@PathVariable @NonNull UUID id, @Valid @RequestBody VoteRequest request, @AuthenticationPrincipal User voter) {
        pollService.vote(id, request, voter);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.success(HttpStatus.CREATED, "Vote recorded successfully"));
    }
}
