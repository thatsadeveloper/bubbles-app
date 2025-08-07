package com.bubbles.exercise;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExerciseAttemptRepository extends JpaRepository<ExerciseAttempt, Long> {

    @Query("select a from ExerciseAttempt a where a.userId = :userId and a.exerciseId in (select e.id from com.bubbles.content.Exercise e where e.bubbleId = :bubbleId)")
    List<ExerciseAttempt> findByBubbleAndUser(@Param("bubbleId") Long bubbleId, @Param("userId") Long userId);
}


