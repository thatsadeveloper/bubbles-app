package com.bubbles.progress;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.bubbles.user.UserRepository;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final JdbcTemplate jdbcTemplate;
    private final SrsService srsService;
    private final UserRepository userRepository;

    public ProgressController(JdbcTemplate jdbcTemplate, SrsService srsService, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.srsService = srsService;
        this.userRepository = userRepository;
    }

    @GetMapping("/due")
    public ResponseEntity<?> due(@AuthenticationPrincipal UserDetails principal) {
        Long userId = principal == null ? null : userRepository.findByEmail(principal.getUsername()).map(u -> u.getId()).orElse(null);
        if (userId == null) return ResponseEntity.status(401).build();
        List<Map<String, Object>> vocabDue = jdbcTemplate.queryForList(
            "select v.id, v.lemma, v.translation from user_vocab_progress p join vocabulary v on p.vocab_id=v.id where p.user_id=? and (p.next_review_at is null or p.next_review_at<=now()) limit 20",
            userId
        );
        List<Map<String, Object>> sentenceDue = jdbcTemplate.queryForList(
            "select s.id, s.text from user_sentence_progress p join sentences s on p.sentence_id=s.id where p.user_id=? and (p.next_review_at is null or p.next_review_at<=now()) limit 20",
            userId
        );
        return ResponseEntity.ok(Map.of("vocab", vocabDue, "sentences", sentenceDue));
    }

    @PostMapping("/review")
    public ResponseEntity<?> review(@AuthenticationPrincipal UserDetails principal, @RequestBody Map<String, Object> body) {
        Long userId = principal == null ? null : userRepository.findByEmail(principal.getUsername()).map(u -> u.getId()).orElse(null);
        if (userId == null) return ResponseEntity.status(401).build();
        String type = (String) body.get("type"); // vocab|sentence
        Long itemId = ((Number) body.get("itemId")).longValue();
        boolean correct = Boolean.TRUE.equals(body.get("correct"));

        String table = type.equals("vocab") ? "user_vocab_progress" : "user_sentence_progress";
        String idColumn = type.equals("vocab") ? "vocab_id" : "sentence_id";

        Map<String, Object> row = jdbcTemplate.queryForMap(
            "select coalesce(easiness,2.5) easiness, coalesce(repetitions,0) repetitions, coalesce(interval_days,0) interval_days from " + table + " where user_id=? and " + idColumn + "=?",
            userId, itemId
        );
        double easiness = ((Number) row.getOrDefault("easiness", 2.5)).doubleValue();
        int repetitions = ((Number) row.getOrDefault("repetitions", 0)).intValue();
        int intervalDays = ((Number) row.getOrDefault("interval_days", 0)).intValue();
        var next = srsService.review(new SrsService.SrsState(easiness, repetitions, intervalDays, Instant.now(), Instant.now()), correct);
        jdbcTemplate.update(
            "insert into " + table + "(user_id," + idColumn + ", easiness, repetitions, interval_days, last_review_at, next_review_at) values (?,?,?,?,?,?,?) on conflict (user_id," + idColumn + ") do update set easiness=excluded.easiness, repetitions=excluded.repetitions, interval_days=excluded.interval_days, last_review_at=excluded.last_review_at, next_review_at=excluded.next_review_at",
            userId, itemId, next.easiness(), next.repetitions(), next.intervalDays(), next.last(), next.next()
        );
        return ResponseEntity.ok(Map.of("status", "ok", "nextReviewAt", next.next()));
    }
}


