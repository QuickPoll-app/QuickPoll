package com.amalitech.quickpoll.controller;

import com.amalitech.quickpoll.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/me")
    @Operation(summary = "Get dashboard for current user")
    public ResponseEntity<ResponseWrapper<Page<PollResponse>>> getMyDashboard(Pageable pageable, @AuthenticationPrincipal User user) {
        Page<PollResponse> polls = dashBoardService.getMyDashboard(pageable, user);
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, polls.isEmpty()? "No polls found" : "Retrieved polls", polls));
    }
    @GetMapping("/active")
    @Operation(summary = "Get all active polls")
    public ResponseEntity<ResponseWrapper<Page<PollResponse>>> getActivePolls(Pageable pageable) {
        Page<PollResponse> polls = dashBoardService.getActivePolls(pageable);
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, polls.isEmpty()? "No active polls found" : "Retrieved active polls", polls));
    }
    @GetMapping("/trending")
    @Operation(summary = "Get trending polls")
    public ResponseEntity<ResponseWrapper<Page<PollResponse>>> getTrendingPolls(Pageable pageable) {
        Page<PollResponse> polls = dashBoardService.getTrendingPolls(pageable);
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, polls.isEmpty()? "No trending polls found" : "Retrieved trending polls", polls));
    }
    
}
