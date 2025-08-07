package com.bubbles.progress;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class SrsService {
    public SrsState review(SrsState state, boolean correct) {
        double easiness = state.easiness;
        int repetitions = state.repetitions;
        int interval = state.intervalDays;

        if (correct) {
            if (repetitions == 0) interval = 1;
            else if (repetitions == 1) interval = 6;
            else interval = (int) Math.round(interval * easiness);
            repetitions += 1;
            easiness = Math.max(1.3, easiness + 0.1);
        } else {
            repetitions = 0;
            interval = 1;
            easiness = Math.max(1.3, easiness - 0.2);
        }
        Instant next = Instant.now().plus(interval, ChronoUnit.DAYS);
        return new SrsState(easiness, repetitions, interval, Instant.now(), next);
    }

    public record SrsState(double easiness, int repetitions, int intervalDays, Instant last, Instant next) {}
}


