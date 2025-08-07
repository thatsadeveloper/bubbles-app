package com.bubbles.exercise;

import org.springframework.stereotype.Service;

@Service
public class ScoringService {

    public boolean fuzzyEquals(String a, String b) {
        if (a == null || b == null) return false;
        String na = normalize(a);
        String nb = normalize(b);
        if (na.equals(nb)) return true;
        int d = levenshtein(na, nb);
        return d <= 2 && Math.abs(na.length() - nb.length()) <= 2;
    }

    private String normalize(String s) {
        return s.toLowerCase().replaceAll("[\\p{Punct}]+", "").replaceAll("\\s+", " ").trim();
    }

    private int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }
}


