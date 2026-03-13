package com.amalitech.quickpoll.controller;

import com.amalitech.quickpoll.dto.*;
import com.amalitech.quickpoll.model.*;
import com.amalitech.quickpoll.repository.PollPerformanceRepository;
import com.amalitech.quickpoll.repository.UserParticipationRepository;
import com.amalitech.quickpoll.repository.VoteTrendRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amalitech.quickpoll.service.DashBoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard management endpoints")
public class DashBoardController {
    private final DashBoardService dashBoardService;
    private final PollPerformanceRepository pollPerformanceRepository;
    private final VoteTrendRepository voteTrendRepository;
    private final UserParticipationRepository userParticipationRepository;

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

    @GetMapping("/poll-performance")
    @Operation(summary = "Get poll performance")
    public ResponseEntity<ResponseWrapper<List<PollPerformanceDto>>> getPollPerformance() {
        List<PollPerformance> data = pollPerformanceRepository.findAll();

        List<PollPerformanceDto> dto = data.stream()
                .map(p -> new PollPerformanceDto(
                        p.getPollId(),
                        p.getTitle(),
                        p.getCreatorName(),
                        p.getTotalVotes(),
                        p.getCreatedAt()
                )).toList();

        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, dto.isEmpty()? "No poll performance found" : "Retrieved poll performance", dto));
    }

    @GetMapping("/vote-trends")
    public ResponseEntity<List<VoteTrendDto>> getVoteTrends() {
        List<VoteTrend> data = voteTrendRepository.findAll();
        List<VoteTrendDto> dto = data.stream()
                .map(v -> new VoteTrendDto(
                        v.getVoteDate(),
                        v.getVotesPerDay()
                )).toList();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user-participation")
    public ResponseEntity<List<UserParticipationDto>> getUserParticipation() {
        List<UserParticipation> data = userParticipationRepository.findAll();
        List<UserParticipationDto> dto = data.stream()
                .map(u -> new UserParticipationDto(
                        u.getVoterName(),
                        u.getTotalVotesCast(),
                        u.getParticipationRate()
                )).toList();

        return ResponseEntity.ok(dto);
    }
}
