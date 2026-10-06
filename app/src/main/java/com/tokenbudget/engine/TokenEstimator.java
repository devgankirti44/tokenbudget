// src/main/java/com/tokenbudget/engine/TokenEstimator.java
package com.tokenbudget.engine;

import org.springframework.stereotype.Component;

@Component
public class TokenEstimator {

    private static final double CHARS_PER_TOKEN = 4.0;
    private static final double CODE_CHARS_PER_TOKEN = 3.5;
    private static final int PROMPT_OVERHEAD_TOKENS = 45;
    private static final double NAIVE_MULTIPLIER_BASE = 6.5;
    private static final int NAIVE_CONTEXT_PREAMBLE = 800;

    /**
     * Estimates tokens for a block of natural-language text.
     * ~1 token per 4 characters is the standard BPE heuristic.
     */
    public int estimateTextTokens(String text) {
        if (text == null || text.isBlank()) return 0;
        String cleaned = text.strip();
        int charCount = cleaned.length();
        int wordCount = cleaned.split("\\s+").length;
        // Hybrid: average character-based and word-based estimates
        int charEstimate = (int) Math.ceil(charCount / CHARS_PER_TOKEN);
        int wordEstimate = (int) Math.ceil(wordCount * 1.33);
        return (charEstimate + wordEstimate) / 2;
    }

    /**
     * Estimates tokens for a step's generated prompt, including system overhead.
     */
    public int estimateStepTokens(String promptText) {
        return estimateTextTokens(promptText) + PROMPT_OVERHEAD_TOKENS;
    }

    /**
     * Estimates what a naive single-dump prompt would cost for the given goal.
     * Accounts for: full goal restatement, context preamble, code context dump,
     * and the tendency to over-explain in a single message.
     */
    public int estimateNaiveDumpTokens(String originalGoal, int stepCount) {
        int goalTokens = estimateTextTokens(originalGoal);
        // Naive prompts repeat goal context, include unnecessary preamble,
        // dump full codebase snippets, and request everything at once.
        double complexityFactor = NAIVE_MULTIPLIER_BASE + (stepCount * 0.8);
        int naiveEstimate = (int)(goalTokens * complexityFactor) + NAIVE_CONTEXT_PREAMBLE;
        // Ensure minimum floor that makes savings meaningful
        return Math.max(naiveEstimate, 2500);
    }

    /**
     * Estimates tokens for code-heavy content (slightly different ratio).
     */
    public int estimateCodeTokens(String code) {
        if (code == null || code.isBlank()) return 0;
        return (int) Math.ceil(code.length() / CODE_CHARS_PER_TOKEN);
    }

    /**
     * Returns a human-readable token count string.
     */
    public static String formatTokenCount(int tokens) {
        if (tokens >= 1000) {
            return String.format("~%.1fk tokens", tokens / 1000.0);
        }
        return String.format("~%d tokens", tokens);
    }
}