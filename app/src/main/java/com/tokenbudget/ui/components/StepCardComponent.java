// src/main/java/com/tokenbudget/ui/components/StepCardComponent.java
package com.tokenbudget.ui.components;

import com.tokenbudget.engine.PromptStep;
import com.tokenbudget.engine.TokenEstimator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class StepCardComponent extends Div {

    public StepCardComponent(PromptStep step, int totalSteps) {
        setWidthFull();
        getStyle()
            .set("background-color", "#ffffff")
            .set("border-radius", "14px")
            .set("padding", "28px")
            .set("margin-bottom", "20px")
            .set("border", "1px solid #ebd9c8")
            .set("box-shadow", "0 4px 18px -4px rgba(30, 28, 25, 0.02)");

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setWidthFull();

        // Header Row
        HorizontalLayout headerRow = new HorizontalLayout();
        headerRow.setWidthFull();
        headerRow.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.BETWEEN);
        headerRow.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);

        // Step Counter Left Row
        HorizontalLayout stepHeaderLeft = new HorizontalLayout();
        stepHeaderLeft.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);
        stepHeaderLeft.setSpacing(true);

        Span stepIndicator = new Span(String.format("PHASE 0%d", step.stepNumber()));
        stepIndicator.getStyle()
            .set("font-size", "10px")
            .set("font-weight", "800")
            .set("letter-spacing", "1px")
            .set("color", "#e05a2b")
            .set("background-color", "#fcf1ec")
            .set("padding", "3px 8px")
            .set("border-radius", "4px");

        H3 title = new H3(step.title());
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "16px")
            .set("font-weight", "800")
            .set("color", "#1e1c19");

        stepHeaderLeft.add(stepIndicator, title);

        // Cost Metadata badge
        Span costMetaBadge = new Span(TokenEstimator.formatTokenCount(step.estimatedTokens()));
        costMetaBadge.getStyle()
            .set("font-size", "11px")
            .set("font-weight", "700")
            .set("color", "#757069")
            .set("background-color", "#faf8f5")
            .set("border", "1px solid #e8e4db")
            .set("padding", "4px 10px")
            .set("border-radius", "100px");

        headerRow.add(stepHeaderLeft, costMetaBadge);

        // Description
        Paragraph desc = new Paragraph(step.description());
        desc.getStyle()
            .set("color", "#5e5952")
            .set("font-size", "13px")
            .set("margin", "12px 0 6px 0")
            .set("line-height", "1.5");

        // Focus Tag
        Span focusAreaTag = new Span("Target File Area: " + step.focusArea());
        focusAreaTag.getStyle()
            .set("font-size", "11px")
            .set("font-weight", "700")
            .set("color", "#a39e95");

        // Interactive Code Preview Window (The prompt payload)
        Div promptPanel = new Div();
        promptPanel.setWidthFull();
        promptPanel.getStyle()
            .set("background-color", "#faf8f5")
            .set("border", "1px solid #ebd9c8")
            .set("border-radius", "10px")
            .set("padding", "20px")
            .set("margin-top", "16px")
            .set("position", "relative");

        Div promptLabel = new Div();
        promptLabel.setText("OPTIMIZED PROMPT CONTEXT REPLICATOR");
        promptLabel.getStyle()
            .set("font-size", "9px")
            .set("font-weight", "800")
            .set("color", "#a39e95")
            .set("letter-spacing", "1px")
            .set("margin-bottom", "12px");

        Div codeContent = new Div();
        codeContent.getStyle()
            .set("font-family", "'Fira Code', 'Consolas', monospace")
            .set("font-size", "12px")
            .set("color", "#1e1c19")
            .set("line-height", "1.6")
            .set("white-space", "pre-wrap")
            .set("overflow-y", "auto")
            .set("max-height", "180px");

        String completePrompt = step.getFormattedPrompt().replace("of N]", "of " + totalSteps + "]");
        codeContent.setText(completePrompt);

        promptPanel.add(promptLabel, codeContent);

        // Single Click Clipboard Copy action Button
        Button copyActionBtn = new Button("Copy Target Prompt Context");
        copyActionBtn.getStyle()
            .set("background-color", "#1e1c19")
            .set("color", "#ffffff")
            .set("border", "none")
            .set("border-radius", "8px")
            .set("font-size", "12px")
            .set("font-weight", "700")
            .set("padding", "10px 18px")
            .set("margin-top", "14px")
            .set("cursor", "pointer")
            .set("transition", "all 0.1s ease");

        copyActionBtn.addClickListener(e -> {
            copyActionBtn.getElement().executeJs(
                "navigator.clipboard.writeText($0).then(() => { " +
                "  $1.textContent = 'Copied to Clipboard!'; " +
                "  $1.style.backgroundColor = '#43b570'; " +
                "  setTimeout(() => { " +
                "    $1.textContent = 'Copy Target Prompt Context'; " +
                "    $1.style.backgroundColor = '#1e1c19'; " +
                "  }, 2000); " +
                "})",
                completePrompt,
                copyActionBtn.getElement()
            );

            Notification copyStatus = Notification.show("Prompt parameters successfully copied to clipboard.", 2000, Notification.Position.BOTTOM_CENTER);
            copyStatus.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        layout.add(headerRow, desc, focusAreaTag, promptPanel, copyActionBtn);
        add(layout);
    }
}