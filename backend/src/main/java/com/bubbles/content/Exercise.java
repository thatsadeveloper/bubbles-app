package com.bubbles.content;

import jakarta.persistence.*;

@Entity
@Table(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bubble_id")
    private Long bubbleId;
    private String type;
    @Column(name = "prompt", columnDefinition = "jsonb")
    private String prompt;
    @Column(name = "solution", columnDefinition = "jsonb")
    private String solution;
    @Column(name = "distractors_json", columnDefinition = "jsonb")
    private String distractorsJson;
    @Column(name = "metadata_json", columnDefinition = "jsonb")
    private String metadataJson;

    public Long getId() { return id; }
    public Long getBubbleId() { return bubbleId; }
    public void setBubbleId(Long bubbleId) { this.bubbleId = bubbleId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }
    public String getDistractorsJson() { return distractorsJson; }
    public void setDistractorsJson(String distractorsJson) { this.distractorsJson = distractorsJson; }
    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
}


