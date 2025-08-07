package com.bubbles.content;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bubbles/{bubbleId}")
public class ContentController {

    private final SentenceRepository sentenceRepository;
    private final VocabularyRepository vocabularyRepository;
    private final ConversationTurnRepository conversationTurnRepository;
    private final ExerciseRepository exerciseRepository;

    public ContentController(SentenceRepository sentenceRepository, VocabularyRepository vocabularyRepository, ConversationTurnRepository conversationTurnRepository, ExerciseRepository exerciseRepository) {
        this.sentenceRepository = sentenceRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.conversationTurnRepository = conversationTurnRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @GetMapping("/sentences")
    public ResponseEntity<List<Sentence>> sentences(@PathVariable Long bubbleId) {
        return ResponseEntity.ok(sentenceRepository.findByBubbleIdOrderByOrderIndexAsc(bubbleId));
    }

    @GetMapping("/vocabulary")
    public ResponseEntity<List<Vocabulary>> vocabulary(@PathVariable Long bubbleId) {
        return ResponseEntity.ok(vocabularyRepository.findByBubbleId(bubbleId));
    }

    @GetMapping("/conversation")
    public ResponseEntity<List<ConversationTurn>> conversation(@PathVariable Long bubbleId) {
        return ResponseEntity.ok(conversationTurnRepository.findByBubbleIdOrderByOrderIndexAsc(bubbleId));
    }

    @GetMapping("/exercises")
    public ResponseEntity<List<Exercise>> exercises(@PathVariable Long bubbleId) {
        return ResponseEntity.ok(exerciseRepository.findByBubbleId(bubbleId));
    }
}


