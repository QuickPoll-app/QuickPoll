package com.amalitech.quickpoll.repository;

import com.amalitech.quickpoll.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, UUID> {
    Page<Poll> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Poll> findByCreatorIdOrderByCreatedAtDesc(UUID creatorId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.option.pollOptionId = :id")
    long countVotesByOptionId(@Param("id") UUID id);
    // TODO: Add search and filter methods
}
