package com.bubbles.content;

import jakarta.persistence.*;

@Entity
@Table(name = "sentences")
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bubble_id")
    private Long bubbleId;

    @Column(columnDefinition = "text")
    private String text;

    @Column(columnDefinition = "text")
    private String translation;

    private Integer difficulty;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "audio_url")
    private String audioUrl;

    public Long getId() { return id; }
    public Long getBubbleId() { return bubbleId; }
    public void setBubbleId(Long bubbleId) { this.bubbleId = bubbleId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }
    public Integer getDifficulty() { return difficulty; }
    public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}


