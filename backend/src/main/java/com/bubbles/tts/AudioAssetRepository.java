package com.bubbles.tts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AudioAssetRepository extends JpaRepository<AudioAsset, Long> {
    List<AudioAsset> findByBubbleId(Long bubbleId);
}


