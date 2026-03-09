package com.amalitech.quickpoll.controller;

import com.amalitech.quickpoll.dto.*;
import com.amalitech.quickpoll.model.User;
import com.amalitech.quickpoll.service.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ResponseWrapper> getAllPolls(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "", pollService.getAllPolls(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper> getPollById(@PathVariable UUID id) {
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "", pollService.getPollById(id)))   ;
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> createPoll(@Valid @RequestBody PollRequest request, @AuthenticationPrincipal User creator) {
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "", pollService.createPoll(request, creator)));
    }

    // TODO: Add vote endpoint - POST /api/polls/{id}/vote
    // TODO: Add close poll endpoint - PUT /api/polls/{id}/close
    // TODO: Add delete poll endpoint - DELETE /api/polls/{id}
    // TODO: Add get results endpoint - GET /api/polls/{id}/results
}
