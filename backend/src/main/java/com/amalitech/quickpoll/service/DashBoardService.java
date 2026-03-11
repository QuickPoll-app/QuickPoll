package com.amalitech.quickpoll.service;

import java.util.List;

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

@Service
@RequiredArgsConstructor
public class DashBoardService {
    private final UserRepository userRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository optionRepository;
    private final VoteRepository voteRepository;

    public Page<PollResponse> getActivePolls(Pageable pageable) {
        return pollRepository.findByStatusOrderByCreatedAtDesc(PollStatus.ACTIVE, pageable).map(this::toResponse);
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
                    .percentage(totalVotes > 0 ? (count * 100.0 / totalVotes) : 0)
                    .build();
        }).collect(java.util.stream.Collectors.toList());
        int uniqueVoters = voteRepository.countDistinctVotersByPollId(poll.getId());
        int participationRate = (uniqueVoters / userRepository.count()) * 100;

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
