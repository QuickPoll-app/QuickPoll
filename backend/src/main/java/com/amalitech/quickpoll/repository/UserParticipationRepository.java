package com.amalitech.quickpoll.repository;

import com.amalitech.quickpoll.model.UserParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserParticipationRepository extends JpaRepository<UserParticipation, Long> {}
