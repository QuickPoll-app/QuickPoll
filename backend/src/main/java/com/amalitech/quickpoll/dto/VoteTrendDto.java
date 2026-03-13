package com.amalitech.quickpoll.dto;

import java.time.LocalDate;

public record VoteTrendDto(
        LocalDate vote_date,
        Integer votes_per_day
) {}
