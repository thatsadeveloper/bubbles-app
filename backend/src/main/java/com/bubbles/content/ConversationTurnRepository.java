package com.bubbles.content;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationTurnRepository extends JpaRepository<ConversationTurn, Long> {
    List<ConversationTurn> findByBubbleIdOrderByOrderIndexAsc(Long bubbleId);
}


