package com.amalitech.quickpoll.repository;

import com.amalitech.quickpoll.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByPoll_Id(Long pollId);
    Optional<Vote> findByUserIdAndPoll_Id(Long userId, Long pollId);
    boolean existsByUserIdAndPoll_Id(Long userId, Long pollId);
    int countByOption_Id(Long optionId);
}
