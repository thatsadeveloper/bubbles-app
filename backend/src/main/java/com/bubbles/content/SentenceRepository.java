package com.bubbles.content;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SentenceRepository extends JpaRepository<Sentence, Long> {
    List<Sentence> findByBubbleIdOrderByOrderIndexAsc(Long bubbleId);
}


