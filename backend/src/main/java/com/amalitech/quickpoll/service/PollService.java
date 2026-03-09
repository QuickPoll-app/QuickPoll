package com.amalitech.quickpoll.service;

import com.amalitech.quickpoll.dto.*;
import com.amalitech.quickpoll.model.*;
import com.amalitech.quickpoll.model.enums.PollStatus;
import com.amalitech.quickpoll.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepository;
    private final PollOptionRepository optionRepository;
    private final VoteRepository voteRepository;

    public Page<PollResponse> getAllPolls(Pageable pageable) {
        return pollRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toResponse);
    }

    public PollResponse getPollById(UUID id) {
        Poll poll = pollRepository.findById(id).orElseThrow(() -> new RuntimeException("Poll not found"));
        return toResponse(poll);
    }

    public PollResponse createPoll(PollRequest request, User creator) {
        Poll poll = Poll.builder()
                .title(request.question())
                .description(request.description())
                .creator(creator)
                .multiSelect(request.multipleChoice())
                .status(PollStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();
        poll = pollRepository.save(poll);

        for (String optionText : request.options()) {
            PollOption option = PollOption.builder()
                    .optionText(optionText)
                    .poll(poll)
                    .build();
            optionRepository.save(option);
        }
        return toResponse(pollRepository.findById(poll.getId()).get());
    }

    // TODO: Implement vote method
    // public void vote(Long pollId, VoteRequest request, User voter) { ... }

    // TODO: Implement closePoll method
    // public PollResponse closePoll(Long pollId, User creator) { ... }

    // TODO: Implement deletePoll method

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

        return PollResponse.builder()
                .id(poll.getId())
                .question(poll.getTitle())
                .description(poll.getDescription())
                .creatorName(poll.getCreator().getFullName())
                .status(poll.getStatus())
                .multipleChoice(poll.isMultiSelect())
                .createdAt(poll.getCreatedAt())
                .totalVotes(totalVotes)
                .options(optionResponses)
                .build();
    }
}
