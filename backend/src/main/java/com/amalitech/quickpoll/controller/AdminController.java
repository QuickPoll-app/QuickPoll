package com.amalitech.quickpoll.controller;

import com.amalitech.quickpoll.dto.AdminStats;
import com.amalitech.quickpoll.dto.PollResponse;
import com.amalitech.quickpoll.dto.ResponseWrapper;
import com.amalitech.quickpoll.service.DashBoardService;
import com.amalitech.quickpoll.service.PollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin dashboard management endpoints")
public class AdminController {
    private final PollService pollService;
    private final DashBoardService dashBoardService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get admin statistics")
    public ResponseEntity<ResponseWrapper<AdminStats>> getAdminStats() {
        AdminStats adminStats = dashBoardService.getAdminStats();
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "Admin stats", adminStats));
    }

    @GetMapping("/polls")
    @Operation(summary = "Get all polls")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<Page<PollResponse>>> getAllPolls(Pageable pageable) {
        Page<PollResponse> polls = pollService.getAllPolls(pageable); // TODO add email
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, polls.isEmpty()? "No polls found" : "Retrieved all polls", polls));
    }
}
