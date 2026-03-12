package com.amalitech.quickpoll.repository;

import com.amalitech.quickpoll.model.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PollOptionRepository extends JpaRepository<PollOption, UUID> {
    List<PollOption> findByPollId(UUID pollId);
    void deleteAllByPollId(UUID pollId);
}
