package com.amalitech.quickpoll.controller;

import com.amalitech.quickpoll.dto.*;
import com.amalitech.quickpoll.model.User;
import com.amalitech.quickpoll.service.PollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
@Tag(name = "Polls", description = "Poll management endpoints")
public class PollController {
    private final PollService pollService;

    @GetMapping
    @Operation(summary = "Get all polls")
    public ResponseEntity<ResponseWrapper<Page<PollResponse>>> getAllPolls(Pageable pageable) {
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "", pollService.getAllPolls(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get poll by ID")
    public ResponseEntity<ResponseWrapper<PollResponse>> getPollById(@PathVariable UUID id) {
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "", pollService.getPollById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new poll")
    public ResponseEntity<ResponseWrapper<PollResponse>> createPoll(@Valid @RequestBody PollRequest request, @AuthenticationPrincipal User creator) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.success(HttpStatus.CREATED, "", pollService.createPoll(request, creator)));
    }

    @PostMapping("/{id}/vote")
    @Operation(summary = "Record a vote for a poll")
    public ResponseEntity<ResponseWrapper<Void>> vote(@PathVariable @NonNull UUID id, @Valid @RequestBody VoteRequest request, @AuthenticationPrincipal User voter) {
        pollService.vote(id, request, voter);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.success(HttpStatus.CREATED, "Vote recorded successfully"));
    }

    // TODO: Add vote endpoint - POST /api/polls/{id}/vote
    // TODO: Add close poll endpoint - PUT /api/polls/{id}/close
    // TODO: Add delete poll endpoint - DELETE /api/polls/{id}
    // TODO: Add get results endpoint - GET /api/polls/{id}/results
}
