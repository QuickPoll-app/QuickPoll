package com.amalitech.quickpoll.repository;

import com.amalitech.quickpoll.model.VoteTrend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteTrendRepository extends JpaRepository<VoteTrend, Long> {}