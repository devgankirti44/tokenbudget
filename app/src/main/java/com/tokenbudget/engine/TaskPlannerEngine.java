// src/main/java/com/tokenbudget/engine/TaskPlannerEngine.java
package com.tokenbudget.engine;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class TaskPlannerEngine {

    private final DecompositionTemplates decompositionTemplates;
    private final TokenEstimator tokenEstimator;

    // Keyword-to-category scoring maps (deterministic classification)
    private static final Map<TaskCategory, List<WeightedPattern>> CLASSIFICATION_RULES = new LinkedHashMap<>();

    record WeightedPattern(Pattern pattern, double weight) {}

    static {
        CLASSIFICATION_RULES.put(TaskCategory.DATABASE_MIGRATION, List.of(
            new WeightedPattern(Pattern.compile("\\bmigrat(e|ion|ing)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\b(mysql|postgres(ql)?|sqlite|mongodb|mariadb|oracle|mssql|h2)\\b", Pattern.CASE_INSENSITIVE), 2.0),
            new WeightedPattern(Pattern.compile("\\b(switch|convert|move)\\s+(to|from)\\s+\\w*\\s*(database|db|sql)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\bschema\\s*(chang|migrat|updat|convert)\\b", Pattern.CASE_INSENSITIVE), 2.5),
            new WeightedPattern(Pattern.compile("\\b(flyway|liquibase)\\b", Pattern.CASE_INSENSITIVE), 2.5),
            new WeightedPattern(Pattern.compile("\\bdatabase\\s*(migration|switch|conversion)\\b", Pattern.CASE_INSENSITIVE), 3.5),
            new WeightedPattern(Pattern.compile("\\borm\\s*(switch|migrat|chang)\\b", Pattern.CASE_INSENSITIVE), 2.5)
        ));

        CLASSIFICATION_RULES.put(TaskCategory.FEATURE_DEVELOPMENT, List.of(
            new WeightedPattern(Pattern.compile("\\b(build|create|implement|develop|add|make)\\b", Pattern.CASE_INSENSITIVE), 2.0),
            new WeightedPattern(Pattern.compile("\\b(feature|functionality|capability|module|component)\\b", Pattern.CASE_INSENSITIVE), 2.5),
            new WeightedPattern(Pattern.compile("\\b(full[- ]?stack|frontend|backend|end[- ]?to[- ]?end)\\b", Pattern.CASE_INSENSITIVE), 2.0),
            new WeightedPattern(Pattern.compile("\\b(auth(entication|orization)?|login|signup|register|jwt|oauth)\\b", Pattern.CASE_INSENSITIVE), 2.0),
            new WeightedPattern(Pattern.compile("\\b(crud|rest\\s*api|endpoint|page|dashboard|form)\\b", Pattern.CASE_INSENSITIVE), 2.0),
            new WeightedPattern(Pattern.compile("\\b(user|admin|role|permission|profile)\\b", Pattern.CASE_INSENSITIVE), 1.5),
            new WeightedPattern(Pattern.compile("\\b(shopping\\s*cart|checkout|payment|notification|search)\\b", Pattern.CASE_INSENSITIVE), 2.5),
            new WeightedPattern(Pattern.compile("\\bnew\\s+(feature|page|endpoint|api|service)\\b", Pattern.CASE_INSENSITIVE), 3.0)
        ));

        CLASSIFICATION_RULES.put(TaskCategory.DEBUGGING_BUGFIX, List.of(
            new WeightedPattern(Pattern.compile("\\b(bug|defect|issue|error|exception|crash)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\b(fix|debug|troubleshoot|diagnose|resolve|patch)\\b", Pattern.CASE_INSENSITIVE), 2.5),
            new WeightedPattern(Pattern.compile("\\b(broken|not\\s+work|fail(s|ing|ed)?|wrong|incorrect)\\b", Pattern.CASE_INSENSITIVE), 2.5),
            new WeightedPattern(Pattern.compile("\\b(null\\s*pointer|stack\\s*overflow|out\\s*of\\s*memory|404|500|timeout)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\b(regression|unexpected|behavior|symptom)\\b", Pattern.CASE_INSENSITIVE), 2.0),
            new WeightedPattern(Pattern.compile("\\bwhy\\s+(does|is|isn't|doesn't)\\b", Pattern.CASE_INSENSITIVE), 2.0)
        ));

        CLASSIFICATION_RULES.put(TaskCategory.CODE_REFACTORING, List.of(
            new WeightedPattern(Pattern.compile("\\brefactor(ing)?\\b", Pattern.CASE_INSENSITIVE), 4.0),
            new WeightedPattern(Pattern.compile("\\b(clean\\s*up|simplify|restructur|reorganiz|modulariz)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\b(extract|inline|rename|move|split|merge)\\s+(class|method|function|module)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\b(code\\s*smell|anti[- ]?pattern|technical\\s*debt|duplication)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\b(solid|dry|kiss|single\\s*responsibility)\\b", Pattern.CASE_INSENSITIVE), 2.0),
            new WeightedPattern(Pattern.compile("\\b(improve|optimize)\\s*(code|structure|architecture|readability)\\b", Pattern.CASE_INSENSITIVE), 2.5)
        ));

        CLASSIFICATION_RULES.put(TaskCategory.API_INTEGRATION, List.of(
            new WeightedPattern(Pattern.compile("\\bintegrat(e|ion|ing)\\b", Pattern.CASE_INSENSITIVE), 2.5),
            new WeightedPattern(Pattern.compile("\\b(third[- ]?party|external)\\s*(api|service|sdk|library)\\b", Pattern.CASE_INSENSITIVE), 3.5),
            new WeightedPattern(Pattern.compile("\\b(stripe|twilio|sendgrid|aws|firebase|google\\s*api|slack\\s*api|github\\s*api)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\b(webhook|callback|rest\\s*client|http\\s*client)\\b", Pattern.CASE_INSENSITIVE), 2.5),
            new WeightedPattern(Pattern.compile("\\b(consume|call|connect\\s*to|hit)\\s*(an?\\s+)?(api|endpoint|service)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\b(api\\s*key|oauth|bearer\\s*token)\\b", Pattern.CASE_INSENSITIVE), 2.0)
        ));

        CLASSIFICATION_RULES.put(TaskCategory.TESTING_QA, List.of(
            new WeightedPattern(Pattern.compile("\\b(test|testing)\\b", Pattern.CASE_INSENSITIVE), 2.5),
            new WeightedPattern(Pattern.compile("\\b(unit\\s*test|integration\\s*test|e2e|end[- ]?to[- ]?end\\s*test)\\b", Pattern.CASE_INSENSITIVE), 3.5),
            new WeightedPattern(Pattern.compile("\\b(junit|mockito|testcontainers|selenium|cypress)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\b(test\\s*coverage|code\\s*coverage|qa|quality\\s*assurance)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\b(tdd|bdd|test[- ]?driven)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\bwrite\\s+tests?\\s+for\\b", Pattern.CASE_INSENSITIVE), 3.5)
        ));

        CLASSIFICATION_RULES.put(TaskCategory.DEPLOYMENT_DEVOPS, List.of(
            new WeightedPattern(Pattern.compile("\\b(deploy|deployment|deploying)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\b(docker|kubernetes|k8s|container|helm)\\b", Pattern.CASE_INSENSITIVE), 3.0),
            new WeightedPattern(Pattern.compile("\\b(ci/?cd|pipeline|github\\s*actions|jenkins|gitlab\\s*ci)\\b", Pattern.CASE_INSENSITIVE), 3.5),
            new WeightedPattern(Pattern.compile("\\b(render|heroku|aws|gcp|azure|vercel|netlify|railway)\\b", Pattern.CASE_INSENSITIVE), 2.5),
            new WeightedPattern(Pattern.compile("\\b(nginx|reverse\\s*proxy|load\\s*balancer|ssl|https)\\b", Pattern.CASE_INSENSITIVE), 2.5),
            new WeightedPattern(Pattern.compile("\\b(infrastructure|devops|hosting|cloud|server)\\b", Pattern.CASE_INSENSITIVE), 2.0)
        ));
    }

    public TaskPlannerEngine(DecompositionTemplates decompositionTemplates, TokenEstimator tokenEstimator) {
        this.decompositionTemplates = decompositionTemplates;
        this.tokenEstimator = tokenEstimator;
    }

    /**
     * Classifies a task goal into the best-matching TaskCategory
     * using weighted keyword/pattern scoring. 100% deterministic.
     */
    public TaskCategory classify(String goalText) {
        if (goalText == null || goalText.isBlank()) {
            return TaskCategory.GENERAL_TECHNICAL_TASK;
        }

        String normalizedGoal = goalText.strip().toLowerCase();
        Map<TaskCategory, Double> scores = new LinkedHashMap<>();

        for (var entry : CLASSIFICATION_RULES.entrySet()) {
            TaskCategory category = entry.getKey();
            double totalScore = 0.0;

            for (WeightedPattern wp : entry.getValue()) {
                var matcher = wp.pattern().matcher(normalizedGoal);
                while (matcher.find()) {
                    totalScore += wp.weight();
                }
            }
            scores.put(category, totalScore);
        }

        // Find the highest scoring category
        TaskCategory bestMatch = TaskCategory.GENERAL_TECHNICAL_TASK;
        double bestScore = 0.0;

        for (var entry : scores.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestScore = entry.getValue();
                bestMatch = entry.getKey();
            }
        }

        // Minimum confidence threshold — if nothing scores above 2.0,
        // fall back to GENERAL_TECHNICAL_TASK
        if (bestScore < 2.0) {
            return TaskCategory.GENERAL_TECHNICAL_TASK;
        }

        return bestMatch;
    }

    /**
     * Full pipeline: classify → decompose → estimate → build blueprint.
     */
    public PromptBlueprint generateBlueprint(String goalText) {
        if (goalText == null || goalText.isBlank()) {
            throw new IllegalArgumentException("Task goal cannot be empty.");
        }

        String cleanedGoal = goalText.strip();

        // Step 1: Classify
        TaskCategory category = classify(cleanedGoal);

        // Step 2: Decompose into steps
        List<PromptStep> steps = decompositionTemplates.generateSteps(category, cleanedGoal);

        // Step 3: Recalculate token estimates with actual prompt content
        List<PromptStep> estimatedSteps = new ArrayList<>();
        for (PromptStep step : steps) {
            int actualTokens = tokenEstimator.estimateStepTokens(step.generatedPrompt());
            // Use the larger of template estimate or actual content estimate
            int finalTokens = Math.max(step.estimatedTokens(), actualTokens);
            estimatedSteps.add(new PromptStep(
                step.stepNumber(),
                step.title(),
                step.description(),
                step.generatedPrompt(),
                finalTokens,
                step.focusArea()
            ));
        }

        // Step 4: Estimate naive cost
        int naiveTokens = tokenEstimator.estimateNaiveDumpTokens(cleanedGoal, estimatedSteps.size());

        // Step 5: Build blueprint
        return PromptBlueprint.create(cleanedGoal, category, estimatedSteps, naiveTokens);
    }

    /**
     * Returns classification scores for transparency/debugging (optional UI use).
     */
    public Map<TaskCategory, Double> getClassificationScores(String goalText) {
        if (goalText == null || goalText.isBlank()) return Map.of();

        String normalizedGoal = goalText.strip().toLowerCase();
        Map<TaskCategory, Double> scores = new LinkedHashMap<>();

        for (var entry : CLASSIFICATION_RULES.entrySet()) {
            double totalScore = 0.0;
            for (WeightedPattern wp : entry.getValue()) {
                var matcher = wp.pattern().matcher(normalizedGoal);
                while (matcher.find()) {
                    totalScore += wp.weight();
                }
            }
            if (totalScore > 0) {
                scores.put(entry.getKey(), totalScore);
            }
        }

        return scores;
    }
}