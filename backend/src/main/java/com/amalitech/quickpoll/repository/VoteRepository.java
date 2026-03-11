package com.amalitech.quickpoll.repository;

import com.amalitech.quickpoll.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {
    List<Vote> findByPoll_Id(UUID pollId);
    Optional<Vote> findByUserIdAndPoll_Id(UUID userId, UUID pollId);
    boolean existsByUserIdAndPoll_Id(UUID userId, UUID pollId);
    boolean existsByUserIdAndOptionPollId(UUID userId, UUID pollId);
    long countDistinctVotersByPollId(UUID pollId);
    int countByOption_Id(UUID optionId);
}
