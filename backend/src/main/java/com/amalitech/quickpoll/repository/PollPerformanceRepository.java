package com.amalitech.quickpoll.repository;

import com.amalitech.quickpoll.model.PollPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollPerformanceRepository extends JpaRepository<PollPerformance, Long> {}
