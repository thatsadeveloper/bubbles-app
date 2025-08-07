package com.bubbles.content;

import jakarta.persistence.*;

@Entity
@Table(name = "vocabulary")
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bubble_id")
    private Long bubbleId;

    private String lemma;
    private String translation;
    private String pos;
    private String gender;

    @Column(name = "example_sentence_id")
    private Long exampleSentenceId;

    private Integer frequency;
    @Column(name = "audio_url")
    private String audioUrl;

    public Long getId() { return id; }
    public Long getBubbleId() { return bubbleId; }
    public void setBubbleId(Long bubbleId) { this.bubbleId = bubbleId; }
    public String getLemma() { return lemma; }
    public void setLemma(String lemma) { this.lemma = lemma; }
    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }
    public String getPos() { return pos; }
    public void setPos(String pos) { this.pos = pos; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Long getExampleSentenceId() { return exampleSentenceId; }
    public void setExampleSentenceId(Long exampleSentenceId) { this.exampleSentenceId = exampleSentenceId; }
    public Integer getFrequency() { return frequency; }
    public void setFrequency(Integer frequency) { this.frequency = frequency; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}


