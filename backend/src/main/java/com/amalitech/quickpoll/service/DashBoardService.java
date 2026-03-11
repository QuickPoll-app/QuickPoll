package com.amalitech.quickpoll.service;

import java.time.Instant;
import java.util.List;

import com.amalitech.quickpoll.dto.AdminStats;
import com.amalitech.quickpoll.exceptionHandler.ResourceNotFoundException;
import com.amalitech.quickpoll.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.amalitech.quickpoll.dto.OptionResponse;
import com.amalitech.quickpoll.dto.PollResponse;
import com.amalitech.quickpoll.model.Poll;
import com.amalitech.quickpoll.model.PollOption;
import com.amalitech.quickpoll.model.enums.PollStatus;
import com.amalitech.quickpoll.repository.PollOptionRepository;
import com.amalitech.quickpoll.repository.PollRepository;
import com.amalitech.quickpoll.repository.UserRepository;
import com.amalitech.quickpoll.repository.VoteRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashBoardService {
    private final UserRepository userRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository optionRepository;
    private final VoteRepository voteRepository;

    @Transactional(readOnly = true)
    public Page<PollResponse> getMyDashboard(Pageable pageable, User user) {
        return pollRepository.findByCreatorIdOrderByCreatedAtDesc(pageable, user.getId()).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AdminStats getAdminStats() {
        long totalPolls = pollRepository.count();
        long totalVotes = voteRepository.count();
        long totalUsers = userRepository.count();
        float participationRate = (totalUsers > 0) ? (totalVotes / (float) totalUsers) * 100.0f : 0;
        return AdminStats.builder()
                .totalPolls(totalPolls)
                .totalVotes(totalVotes)
                .totalUsers(totalUsers)
                .participationRate(participationRate)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PollResponse> getTrendingPolls(Pageable pageable) {
        return pollRepository.getTrendingPolls(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PollResponse> getActivePolls(Pageable pageable) {
        return pollRepository.getActivePolls(PollStatus.ACTIVE, Instant.now(), pageable).map(this::toResponse);
    }

    private PollResponse toResponse(Poll poll) {
        List<PollOption> options = optionRepository.findByPollId(poll.getId());
        int totalVotes = options.stream().mapToInt(o -> voteRepository.countByOption_Id(o.getId())).sum();
        List<OptionResponse> optionResponses = options.stream().map(o -> {
            int count = voteRepository.countByOption_Id(o.getId());
            return OptionResponse.builder()
                    .id(o.getId())
                    .text(o.getOptionText())
                    .voteCount(count)
                    .percentage(totalVotes > 0 ? (count / (double) totalVotes) * 100.0 : 0)
                    .build();
        }).collect(java.util.stream.Collectors.toList());
        long uniqueVoters = voteRepository.countDistinctVotersByPollId(poll.getId());
        long users = userRepository.count();
        float participationRate = (uniqueVoters > 0) ? (uniqueVoters / (float) users) * 100.0f : 0;

        return PollResponse.builder()
                .id(poll.getId())
                .question(poll.getTitle())
                .description(poll.getDescription())
                .creatorName(poll.getCreator().getFullName())
                .status(poll.getStatus())
                .multipleChoice(poll.isMultiSelect())
                .createdAt(poll.getCreatedAt())
                .expiresAt(poll.getExpiresAt())
                .totalVotes(totalVotes)
                .participationRate(participationRate)
                .options(optionResponses)
                .build();
    }
}
