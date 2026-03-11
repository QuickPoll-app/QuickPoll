package com.amalitech.quickpoll.controller;

import com.amalitech.quickpoll.dto.AdminStats;
import com.amalitech.quickpoll.dto.PollResponse;
import com.amalitech.quickpoll.dto.ResponseWrapper;
import com.amalitech.quickpoll.service.DashBoardService;
import com.amalitech.quickpoll.service.PollService;
import com.amalitech.quickpoll.service.UserService;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management endpoints")
public class AdminController {
    private final UserService userService;
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
        Page<PollResponse> polls = pollService.getAllPolls(pageable);
        if (polls.isEmpty()) {
            return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "No polls found", polls));
        }
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "Retrieved all polls", polls));
    }
}
