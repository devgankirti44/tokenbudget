package com.tokenbudget.engine;

import java.time.LocalDateTime;
import java.util.List;

public record PromptBlueprint(
    String originalGoal,
    TaskCategory category,
    List<PromptStep> steps,
    int totalOptimizedTokens,
    int estimatedNaiveTokens,
    double savingsPercentage,
    LocalDateTime generatedAt
) {
    public static PromptBlueprint create(
            String originalGoal,
            TaskCategory category,
            List<PromptStep> steps,
            int estimatedNaiveTokens) {

        int totalOptimized = steps.stream()
                .mapToInt(PromptStep::estimatedTokens)
                .sum();

        double savings = estimatedNaiveTokens > 0
                ? ((double)(estimatedNaiveTokens - totalOptimized) / estimatedNaiveTokens) * 100.0
                : 0.0;

        return new PromptBlueprint(
            originalGoal,
            category,
            steps,
            totalOptimized,
            estimatedNaiveTokens,
            Math.max(0, Math.min(savings, 99.9)),
            LocalDateTime.now()
        );
    }

    public int stepCount() {
        return steps.size();
    }

    public int tokensSaved() {
        return estimatedNaiveTokens - totalOptimizedTokens;
    }
}