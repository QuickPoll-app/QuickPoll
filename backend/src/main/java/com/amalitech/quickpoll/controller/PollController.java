package com.amalitech.quickpoll.controller;

import com.amalitech.quickpoll.dto.*;
import com.amalitech.quickpoll.model.User;
import com.amalitech.quickpoll.service.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
public class PollController {
    private final PollService pollService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<PollResponse>>> getAllPolls(Pageable pageable) {
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "", pollService.getAllPolls(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<PollResponse>> getPollById(@PathVariable UUID id) {
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "", pollService.getPollById(id)));
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<PollResponse>> createPoll(@Valid @RequestBody PollRequest request, @AuthenticationPrincipal User creator) {
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "", pollService.createPoll(request, creator)));
    }

    // TODO: Add vote endpoint - POST /api/polls/{id}/vote
    // TODO: Add close poll endpoint - PUT /api/polls/{id}/close
    // TODO: Add delete poll endpoint - DELETE /api/polls/{id}
    // TODO: Add get results endpoint - GET /api/polls/{id}/results
}
