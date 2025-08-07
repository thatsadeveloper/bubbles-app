package com.bubbles.exercise;

import jakarta.persistence.*;

@Entity
@Table(name = "exercise_attempts")
public class ExerciseAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exercise_id")
    private Long exerciseId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "response_json", columnDefinition = "jsonb")
    private String responseJson;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    private Double score;
    @Column(name = "time_spent_ms")
    private Integer timeSpentMs;

    public Long getId() { return id; }
    public Long getExerciseId() { return exerciseId; }
    public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getResponseJson() { return responseJson; }
    public void setResponseJson(String responseJson) { this.responseJson = responseJson; }
    public Boolean getCorrect() { return isCorrect; }
    public void setCorrect(Boolean correct) { isCorrect = correct; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public Integer getTimeSpentMs() { return timeSpentMs; }
    public void setTimeSpentMs(Integer timeSpentMs) { this.timeSpentMs = timeSpentMs; }
}


