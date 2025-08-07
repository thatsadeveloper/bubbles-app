package com.bubbles.content;

import jakarta.persistence.*;

@Entity
@Table(name = "conversation_turns")
public class ConversationTurn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bubble_id")
    private Long bubbleId;

    private String speaker;
    @Column(columnDefinition = "text")
    private String text;
    @Column(columnDefinition = "text")
    private String translation;
    @Column(name = "order_index")
    private Integer orderIndex;
    @Column(name = "audio_url")
    private String audioUrl;

    public Long getId() { return id; }
    public Long getBubbleId() { return bubbleId; }
    public void setBubbleId(Long bubbleId) { this.bubbleId = bubbleId; }
    public String getSpeaker() { return speaker; }
    public void setSpeaker(String speaker) { this.speaker = speaker; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}


