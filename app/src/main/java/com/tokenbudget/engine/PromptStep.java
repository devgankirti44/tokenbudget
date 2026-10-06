package com.tokenbudget.engine;

public record PromptStep(
    int stepNumber,
    String title,
    String description,
    String generatedPrompt,
    int estimatedTokens,
    String focusArea
) {
    public String getFormattedPrompt() {
        return """
            [Step %d of N] — %s
            
            %s
            
            Focus: %s
            
            Please provide only the code and explanation relevant to this specific step. Do not anticipate or implement future steps.
            """.formatted(stepNumber, title, generatedPrompt, focusArea);
    }
}