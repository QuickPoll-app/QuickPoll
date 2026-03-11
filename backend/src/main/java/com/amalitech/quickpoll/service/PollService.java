package com.amalitech.quickpoll.service;

import com.amalitech.quickpoll.dto.*;
import com.amalitech.quickpoll.exceptionHandler.BadRequestException;
import com.amalitech.quickpoll.exceptionHandler.DuplicateResourceException;
import com.amalitech.quickpoll.exceptionHandler.ResourceNotFoundException;
import com.amalitech.quickpoll.model.*;
import com.amalitech.quickpoll.model.enums.PollStatus;
import com.amalitech.quickpoll.model.enums.Role;
import com.amalitech.quickpoll.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollService {
    private final UserRepository userRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository optionRepository;
    private final VoteRepository voteRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "polls", key = "'page_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public PageResponse<PollResponse> getAllPolls(Pageable pageable) {
       Page<PollResponse> polls = pollRepository.findAll(pageable).map(this::toResponse);
       return PageResponse.from(polls);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "polls", key = "'id_' + #id")
    public PollResponse getPollById(UUID id) {
        Poll poll = pollRepository.findById(id).orElseThrow(() -> new RuntimeException("Poll not found"));
        return toResponse(poll);
    }

    @Transactional
    @CacheEvict(cacheNames = "polls", allEntries = true)
    public PollResponse createPoll(PollRequest request, User creator) {
        Poll poll = Poll.builder()
                .title(request.question())
                .description(request.description())
                .creator(creator)
                .multiSelect(request.multipleChoice())
                .status(PollStatus.ACTIVE)
                .createdAt(Instant.now())
                .expiresAt(request.expiresAt())
                .build();
        if (poll == null) throw new IllegalStateException("Failed to create poll");
        poll = pollRepository.save(poll);
        for (String optionText : request.options()) {
            PollOption pollOption = PollOption.builder().optionText(optionText).poll(poll).build();
            if (pollOption == null) throw new IllegalStateException("Failed to create poll option");
            optionRepository.save(pollOption);
        }
        return toResponse(pollRepository.findById(poll.getId()).orElseThrow(() -> new ResourceNotFoundException("Poll not found")));
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "polls", allEntries = true)
    public PollResponse editPoll(@NonNull UUID pollId, PollRequest request, User creator) {
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ResourceNotFoundException("Poll not found"));
        if (creator.getRole() == Role.USER) {
            if (!poll.getCreator().getId().equals(creator.getId())) throw new IllegalStateException("You are not the creator of this poll");
            if (voteRepository.existsByPollId(pollId)) throw new IllegalStateException("Cannot edit poll with votes");
        }
        poll.setTitle(request.question());
        poll.setDescription(request.description());
        poll.setMultiSelect(request.multipleChoice());
        poll.setExpiresAt(request.expiresAt());
        pollRepository.save(poll);
        optionRepository.deleteAllByPollId(pollId);
        for (String optionText : request.options()) {
            PollOption pollOption = PollOption.builder().optionText(optionText).poll(poll).build();
            if (pollOption == null) throw new IllegalStateException("Failed to create poll option");
            optionRepository.save(pollOption);
        }
        return toResponse(pollRepository.findById(pollId).orElseThrow(() -> new ResourceNotFoundException("Poll not found")));
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "polls", allEntries = true)
    public void vote(@NonNull UUID pollId, VoteRequest request, User voter) {
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ResourceNotFoundException("Poll not found"));
        if (!poll.getStatus().equals(PollStatus.ACTIVE)) throw new IllegalStateException("Poll is closed");
        if (poll.getExpiresAt().isBefore(Instant.now())) throw new IllegalStateException("Poll has expired");
        if (voteRepository.existsByUserIdAndOptionPollId(voter.getId(), pollId)) throw new DuplicateResourceException("User has already voted for this poll");
        if (poll.isMultiSelect()) {
            for (UUID optionId : request.optionIds()) {
                PollOption option = optionRepository.findById(optionId).orElseThrow(() -> new ResourceNotFoundException("Option not found"));
                if (!option.getPoll().getId().equals(pollId)) throw new BadRequestException("Option does not belong to this poll");
                voteRepository.save(Vote.builder().poll(poll).option(option).user(voter).build());
            }
        } else {
            if (request.optionIds().size() > 1) throw new BadRequestException("Multiple options not allowed for this poll");
            PollOption option = optionRepository.findById(request.optionIds().iterator().next()).orElseThrow(() -> new ResourceNotFoundException("Option not found"));
            if (!option.getPoll().getId().equals(pollId)) throw new BadRequestException("Option does not belong to this poll");
            voteRepository.save(Vote.builder().poll(poll).option(option).user(voter).build());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "polls", allEntries = true)
    public PollResponse closePoll(@NonNull UUID pollId, User creator) {
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ResourceNotFoundException("Poll not found"));
        if (!poll.getCreator().getId().equals(creator.getId())) throw new IllegalStateException("You are not the creator of this poll");
        poll.setStatus(PollStatus.CLOSED);
        return toResponse(pollRepository.save(poll));
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "polls", allEntries = true)
    public void deletePoll(@NonNull UUID pollId, User creator) {
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ResourceNotFoundException("Poll not found"));
        if (creator.getRole() == Role.USER) {
            if (!poll.getCreator().getId().equals(creator.getId())) throw new IllegalStateException("You are not the creator of this poll");
        }
        pollRepository.delete(poll);
        optionRepository.deleteAllByPollId(pollId);
        voteRepository.deleteAllByPollId(pollId);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "polls", allEntries = true)
    public void expirePolls() {
        List<Poll> polls = pollRepository.findAllByStatusAndExpiresAtBefore(PollStatus.ACTIVE, Instant.now());
        for (Poll poll : polls) {
            poll.setStatus(PollStatus.CLOSED);
            pollRepository.save(poll);
        }
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
        long uniqueVoters = voteRepository.countDistinctVotersByPollId(poll.getId());
        float participationRate = (uniqueVoters > 0) ? (uniqueVoters / (float) userRepository.count()) * 100.0f : 0;
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
