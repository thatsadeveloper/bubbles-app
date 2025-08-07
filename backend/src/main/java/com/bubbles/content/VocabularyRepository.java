package com.bubbles.content;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    List<Vocabulary> findByBubbleId(Long bubbleId);
}


