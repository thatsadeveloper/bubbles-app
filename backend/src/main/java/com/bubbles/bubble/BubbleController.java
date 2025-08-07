package com.bubbles.bubble;

import com.bubbles.generation.GenerationService;
import com.bubbles.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;

record CreateBubbleRequest(@NotBlank String topic,
                          @Pattern(regexp = "[A-Za-z]{2}") String targetLanguage,
                          @NotBlank String level) {}

@RestController
@RequestMapping("/api/bubbles")
public class BubbleController {
    private final BubbleRepository bubbleRepository;
    private final GenerationService generationService;
    private final UserRepository userRepository;

    public BubbleController(BubbleRepository bubbleRepository, GenerationService generationService, UserRepository userRepository) {
        this.bubbleRepository = bubbleRepository;
        this.generationService = generationService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Bubble> create(@AuthenticationPrincipal UserDetails user,
                                         @Valid @RequestBody CreateBubbleRequest request) {
        final Bubble toCreate = new Bubble();
        if (user != null) {
            userRepository.findByEmail(user.getUsername()).ifPresent(u -> toCreate.setUserId(u.getId()));
        }
        toCreate.setTopic(request.topic());
        toCreate.setTargetLanguage(request.targetLanguage());
        toCreate.setLevel(request.level());
        toCreate.setStatus("PENDING");
        final Bubble saved = bubbleRepository.save(toCreate);

        Executors.newSingleThreadExecutor().submit(() -> generationService.generateForBubble(saved));
        return ResponseEntity.accepted().body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bubble> get(@PathVariable Long id) {
        return bubbleRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> list(@AuthenticationPrincipal UserDetails user) {
        if (user == null) return ResponseEntity.ok(bubbleRepository.findAll());
        return userRepository.findByEmail(user.getUsername())
            .map(u -> ResponseEntity.ok(bubbleRepository.findByUserIdOrderByIdDesc(u.getId())))
            .orElse(ResponseEntity.ok(bubbleRepository.findAll()));
    }

    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events(@PathVariable Long id) throws IOException {
        SseEmitter emitter = new SseEmitter(Duration.ofMinutes(30).toMillis());
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while (true) {
                    Bubble b = bubbleRepository.findById(id).orElse(null);
                    if (b == null) {
                        emitter.completeWithError(new IllegalStateException("bubble not found"));
                        return;
                    }
                    emitter.send(SseEmitter.event().name("status").data(b.getStatus()));
                    if ("COMPLETED".equals(b.getStatus()) || "FAILED".equals(b.getStatus())) {
                        emitter.complete();
                        return;
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }
}


