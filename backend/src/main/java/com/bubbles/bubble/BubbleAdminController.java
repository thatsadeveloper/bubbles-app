package com.bubbles.bubble;

import com.bubbles.generation.GenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/admin/bubbles")
public class BubbleAdminController {
    private final BubbleRepository bubbleRepository;
    private final GenerationService generationService;

    public BubbleAdminController(BubbleRepository bubbleRepository, GenerationService generationService) {
        this.bubbleRepository = bubbleRepository;
        this.generationService = generationService;
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<?> retry(@PathVariable Long id) {
        return bubbleRepository.findById(id).map(b -> {
            b.setStatus("PENDING");
            bubbleRepository.save(b);
            Executors.newSingleThreadExecutor().submit(() -> generationService.generateForBubble(b));
            return ResponseEntity.accepted().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}


