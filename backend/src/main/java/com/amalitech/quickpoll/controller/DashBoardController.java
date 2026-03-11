package com.amalitech.quickpoll.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amalitech.quickpoll.model.Poll;
import com.amalitech.quickpoll.dto.PollResponse;
import com.amalitech.quickpoll.dto.ResponseWrapper;
import com.amalitech.quickpoll.service.DashBoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard management endpoints")
public class DashBoardController {
    private final DashBoardService dashBoardService;

    @GetMapping("/active")
    @Operation(summary = "Get all active polls")
    public ResponseEntity<ResponseWrapper<Page<PollResponse>>> getActivePolls(Pageable pageable) {
        Page<Poll> polls = dashBoardService.getActivePolls(pageable);
        if (polls.isEmpty()) {
            return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "No active polls found", polls));
        }
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "Retrieved active polls", polls));
    }
    @GetMapping("/trending")
    @Operation(summary = "Get trending polls")
     getTrendingPolls() {

    }
    
}
