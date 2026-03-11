package com.amalitech.quickpoll.repository;

import com.amalitech.quickpoll.model.Poll;
import com.amalitech.quickpoll.model.enums.PollStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, UUID> {
    Page<Poll> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Poll> findByCreatorIdOrderByCreatedAtDesc(Pageable pageable, UUID creatorId);
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.option.id = :id")
    long countVotesByOptionId(@Param("id") UUID id);
    // poll that are not expired and not closed
    @Query("SELECT p FROM Poll p WHERE p.status = :status AND p.expiresAt > :now ORDER BY p.createdAt DESC")
    Page<Poll> getActivePolls(PollStatus status, Instant now, Pageable pageable);
    // polls that have the most votes
    @Query("SELECT p FROM Poll p ORDER BY (SELECT COUNT(v) FROM Vote v WHERE v.option.poll.id = p.id) DESC")
    Page<Poll> getTrendingPolls(Pageable pageable);
    // TODO: Add search and filter methods
}
