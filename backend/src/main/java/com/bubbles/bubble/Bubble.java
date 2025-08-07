package com.bubbles.bubble;

import jakarta.persistence.*;

@Entity
@Table(name = "bubbles")
public class Bubble {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    private String topic;
    @Column(name = "target_language")
    private String targetLanguage;
    private String level;
    private String status;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String targetLanguage) { this.targetLanguage = targetLanguage; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


