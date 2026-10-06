// src/main/java/com/tokenbudget/ui/components/SavingsBarComponent.java
package com.tokenbudget.ui.components;

import com.tokenbudget.engine.PromptBlueprint;
import com.tokenbudget.engine.TokenEstimator;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class SavingsBarComponent extends Div {

    public SavingsBarComponent(PromptBlueprint blueprint) {
        setWidthFull();
        getStyle()
            .set("background-color", "#1e1c19") // Rich Premium Charcoal-Dark
            .set("border-radius", "16px")
            .set("padding", "32px")
            .set("color", "#ffffff")
            .set("margin-bottom", "32px")
            .set("box-shadow", "0 20px 40px -15px rgba(30, 28, 25, 0.15)");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setWidthFull();

        // Header Title
        HorizontalLayout headerRow = new HorizontalLayout();
        headerRow.setWidthFull();
        headerRow.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.BETWEEN);
        headerRow.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);

        Span categoryTag = new Span(blueprint.category().getEmoji() + " " + blueprint.category().getDisplayName().toUpperCase());
        categoryTag.getStyle()
            .set("font-size", "11px")
            .set("font-weight", "800")
            .set("letter-spacing", "1.5px")
            .set("color", "#ffaa7a") // Soft elegant orange highlight
            .set("background-color", "#2d2722")
            .set("padding", "4px 12px")
            .set("border-radius", "100px");

        Paragraph metaTitle = new Paragraph("TOKEN ECONOMY COMPARISON");
        metaTitle.getStyle()
            .set("font-size", "11px")
            .set("font-weight", "800")
            .set("letter-spacing", "1px")
            .set("color", "#a39e95")
            .set("margin", "0");

        headerRow.add(metaTitle, categoryTag);

        // Core Comparison Visualizer Block
        HorizontalLayout visualComparisonRow = new HorizontalLayout();
        visualComparisonRow.setWidthFull();
        visualComparisonRow.getStyle().set("margin", "24px 0");
        visualComparisonRow.setSpacing(true);

        // Left Side: Naive Bar
        VerticalLayout leftTrack = createTrackBar(
            "NAIVE SINGLE DUMP PROMPT (Bloated & High-Cost)", 
            blueprint.estimatedNaiveTokens(), 
            "100%", 
            "#e65345", 
            "Raw code dump, loose objectives, immediate hallucination traps."
        );

        // Right Side: Optimized Bar
        double percentageOfOriginal = ((double) blueprint.totalOptimizedTokens() / blueprint.estimatedNaiveTokens()) * 100.0;
        VerticalLayout rightTrack = createTrackBar(
            "TOKENBUDGET SEQUENTIAL PROMPTING (Highly Optimized)", 
            blueprint.totalOptimizedTokens(), 
            String.format("%.1f%%", percentageOfOriginal), 
            "#43b570", 
            "Segmented phases, modular execution context, minimal response drift."
        );

        visualComparisonRow.add(leftTrack, rightTrack);

        // Savings Spotlight Footer
        Div savingsBanner = new Div();
        savingsBanner.setWidthFull();
        savingsBanner.getStyle()
            .set("background-color", "rgba(255, 255, 255, 0.04)")
            .set("border", "1px dashed rgba(255, 255, 255, 0.1)")
            .set("border-radius", "10px")
            .set("padding", "16px 20px")
            .set("display", "flex")
            .set("justify-content-between", "space-between")
            .set("align-items", "center")
            .set("margin-top", "8px");

        Span savingsHighlight = new Span(String.format("SAVINGS ADVANTAGE: %.1f%%", blueprint.savingsPercentage()));
        savingsHighlight.getStyle()
            .set("font-size", "14px")
            .set("font-weight", "800")
            .set("color", "#ffaa7a");

        Span tokensSavedText = new Span(String.format("Avoided dumping %s unnecessary context tokens", 
            TokenEstimator.formatTokenCount(blueprint.tokensSaved())));
        tokensSavedText.getStyle()
            .set("font-size", "12px")
            .set("color", "#a39e95")
            .set("font-weight", "500");

        savingsBanner.add(savingsHighlight, tokensSavedText);

        layout.add(headerRow, visualComparisonRow, savingsBanner);
        add(layout);
    }

    private VerticalLayout createTrackBar(String label, int tokens, String barWidth, String barColor, String description) {
        VerticalLayout track = new VerticalLayout();
        track.setPadding(false);
        track.setSpacing(false);
        track.setWidthFull();

        Paragraph titleLabel = new Paragraph(label);
        titleLabel.getStyle()
            .set("font-size", "10px")
            .set("font-weight", "700")
            .set("color", "#a39e95")
            .set("margin", "0 0 6px 0");

        // The Track bar
        Div containerTrack = new Div();
        containerTrack.setWidthFull();
        containerTrack.getStyle()
            .set("height", "14px")
            .set("background-color", "#2a2824")
            .set("border-radius", "6px")
            .set("overflow", "hidden")
            .set("position", "relative");

        Div filledIndicator = new Div();
        filledIndicator.getStyle()
            .set("width", barWidth)
            .set("height", "100%")
            .set("background-color", barColor)
            .set("border-radius", "6px")
            .set("transition", "width 0.8s cubic-bezier(0.16, 1, 0.3, 1)");

        containerTrack.add(filledIndicator);

        HorizontalLayout valueRow = new HorizontalLayout();
        valueRow.getStyle().set("margin-top", "6px");
        
        Span tokenVal = new Span(TokenEstimator.formatTokenCount(tokens));
        tokenVal.getStyle()
            .set("font-size", "18px")
            .set("font-weight", "800")
            .set("color", "#ffffff");

        valueRow.add(tokenVal);

        Paragraph desc = new Paragraph(description);
        desc.getStyle()
            .set("font-size", "11px")
            .set("color", "#757069")
            .set("margin", "4px 0 0 0")
            .set("line-height", "1.4");

        track.add(titleLabel, containerTrack, valueRow, desc);
        return track;
    }
}