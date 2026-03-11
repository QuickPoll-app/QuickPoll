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
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "Polls retrieved successfully", pollService.getAllPolls(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get poll by ID")
    public ResponseEntity<ResponseWrapper<PollResponse>> getPollById(@PathVariable UUID id) {
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "Poll retrieved successfully", pollService.getPollById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new poll")
    public ResponseEntity<ResponseWrapper<PollResponse>> createPoll(@Valid @RequestBody PollRequest request, @AuthenticationPrincipal User creator) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.success(HttpStatus.CREATED, "Poll created successfully", pollService.createPoll(request, creator)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit a poll")
    public ResponseEntity<ResponseWrapper<PollResponse>> editPoll(@PathVariable @NonNull UUID id, @Valid @RequestBody PollRequest request, @AuthenticationPrincipal User creator) {
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "Poll edited successfully", pollService.editPoll(id, request, creator)));
    }

    @PostMapping("/{id}/vote")
    @Operation(summary = "Record a vote for a poll")
    public ResponseEntity<ResponseWrapper<Void>> vote(@PathVariable @NonNull UUID id, @Valid @RequestBody VoteRequest request, @AuthenticationPrincipal User voter) {
        pollService.vote(id, request, voter);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.success(HttpStatus.CREATED, "Vote recorded successfully"));
    }

    @PutMapping("/{id}/close")
    @Operation(summary = "Close a poll")
    public ResponseEntity<ResponseWrapper<PollResponse>> closePoll(@PathVariable @NonNull UUID id, @AuthenticationPrincipal User creator) {
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "", pollService.closePoll(id, creator)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a poll")
    public ResponseEntity<ResponseWrapper<Void>> deletePoll(@PathVariable @NonNull UUID id, @AuthenticationPrincipal User creator) {
        pollService.deletePoll(id, creator);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseWrapper.success(HttpStatus.NO_CONTENT, "Poll deleted successfully"));
    }
}
