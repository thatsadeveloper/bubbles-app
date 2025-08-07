package com.bubbles.bubble;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BubbleRepository extends JpaRepository<Bubble, Long> {
    List<Bubble> findByUserIdOrderByIdDesc(Long userId);
}


