package com.bubbles.tts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audio")
public class AudioController {
    private final AudioAssetRepository audioAssetRepository;

    public AudioController(AudioAssetRepository audioAssetRepository) {
        this.audioAssetRepository = audioAssetRepository;
    }

    @GetMapping("/bubbles/{bubbleId}")
    public ResponseEntity<List<AudioAsset>> byBubble(@PathVariable Long bubbleId) {
        return ResponseEntity.ok(audioAssetRepository.findByBubbleId(bubbleId));
    }
}


