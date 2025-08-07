package com.bubbles.exercise;

import com.bubbles.content.ExerciseRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import com.bubbles.user.UserRepository;

record AttemptRequest(@NotNull Map<String, Object> response, Integer timeSpentMs) {}
record AttemptResponse(boolean correct, double score, long attemptId) {}

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    private final ExerciseAttemptRepository attemptRepository;
    private final ExerciseRepository exerciseRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScoringService scoringService;
    private final UserRepository userRepository;

    public ExerciseController(ExerciseAttemptRepository attemptRepository, ExerciseRepository exerciseRepository, ScoringService scoringService, UserRepository userRepository) {
        this.attemptRepository = attemptRepository;
        this.exerciseRepository = exerciseRepository;
        this.scoringService = scoringService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{id}/attempts")
    public ResponseEntity<AttemptResponse> attempt(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserDetails user,
                                                   @Valid @RequestBody AttemptRequest request) throws Exception {
        var ex = exerciseRepository.findById(id).orElse(null);
        if (ex == null) return ResponseEntity.notFound().build();
        JsonNode userResp = objectMapper.valueToTree(request.response());
        JsonNode solution = objectMapper.readTree(ex.getSolution());
        boolean correct = userResp.equals(solution);
        if (!correct && ex.getType().equalsIgnoreCase("DICTATION")) {
            String expected = solution.path("answer").asText("");
            String got = userResp.path("answer").asText("");
            correct = scoringService.fuzzyEquals(expected, got);
        }
        double score = correct ? 1.0 : 0.0;

        ExerciseAttempt at = new ExerciseAttempt();
        at.setExerciseId(id);
        if (user != null) {
            var userId = userRepository.findByEmail(user.getUsername()).map(u -> u.getId()).orElse(null);
            at.setUserId(userId);
        }
        at.setResponseJson(objectMapper.writeValueAsString(request.response()));
        at.setCorrect(correct);
        at.setScore(score);
        at.setTimeSpentMs(request.timeSpentMs() == null ? 0 : request.timeSpentMs());
        attemptRepository.save(at);

        return ResponseEntity.ok(new AttemptResponse(correct, score, at.getId()));
    }

    @GetMapping("/bubbles/{bubbleId}/attempts")
    public ResponseEntity<?> byBubble(@PathVariable Long bubbleId, @AuthenticationPrincipal UserDetails user) {
        if (user == null) return ResponseEntity.status(401).build();
        var userId = userRepository.findByEmail(user.getUsername()).map(u -> u.getId()).orElse(null);
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(attemptRepository.findByBubbleAndUser(bubbleId, userId));
    }
}


