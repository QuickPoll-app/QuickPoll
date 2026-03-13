package com.amalitech.quickpoll.dto;

public record UserParticipationDto(
        String voter_name,
        Integer total_votes_cast,
        Double participation_rate
) {}