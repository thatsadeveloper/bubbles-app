package com.bubbles.tts;

import jakarta.persistence.*;

@Entity
@Table(name = "audio_assets")
public class AudioAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bubble_id")
    private Long bubbleId;

    @Column(name = "item_type")
    private String itemType;

    @Column(name = "item_id")
    private Long itemId;

    private String provider;
    private String voice;

    // Will store served endpoint URL, e.g. /api/tts/assets/{id}
    private String url;

    @Column(name = "duration_ms")
    private Integer durationMs;

    private String checksum;

    // Absolute file path on disk where audio is stored
    @Transient
    private String filePath;

    public Long getId() { return id; }
    public Long getBubbleId() { return bubbleId; }
    public void setBubbleId(Long bubbleId) { this.bubbleId = bubbleId; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getVoice() { return voice; }
    public void setVoice(String voice) { this.voice = voice; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Integer getDurationMs() { return durationMs; }
    public void setDurationMs(Integer durationMs) { this.durationMs = durationMs; }
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}


