package com.bubbles.auth;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "verification_tokens")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false, unique = true)
    private String token;
    @Column(nullable = false)
    private Instant expiresAt;
    @Column(nullable = false)
    private boolean used = false;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}


