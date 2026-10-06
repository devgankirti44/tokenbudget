// src/main/java/com/tokenbudget/ui/components/BlueprintResultComponent.java
package com.tokenbudget.ui.components;

import com.tokenbudget.engine.PromptBlueprint;
import com.tokenbudget.engine.PromptStep;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class BlueprintResultComponent extends VerticalLayout {

    public BlueprintResultComponent(PromptBlueprint blueprint) {
        setWidthFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.CENTER);

        // Comparative visual track top
        SavingsBarComponent savings = new SavingsBarComponent(blueprint);
        savings.setWidthFull();
        add(savings);

        // Step details head title layout
        Div stepSectionHeading = new Div();
        stepSectionHeading.setWidthFull();
        stepSectionHeading.getStyle()
            .set("margin-top", "16px")
            .set("margin-bottom", "20px");

        H2 stepTitle = new H2("PHASE-BY-PHASE EXECUTION MAP");
        stepTitle.getStyle()
            .set("font-size", "14px")
            .set("font-weight", "800")
            .set("letter-spacing", "1px")
            .set("color", "#757069")
            .set("margin", "0");

        Paragraph subhead = new Paragraph("Run these modular prompts sequentially. Complete the previous context loop before initiating the next.");
        subhead.getStyle()
            .set("font-size", "13px")
            .set("color", "#a39e95")
            .set("margin", "4px 0 0 0");

        stepSectionHeading.add(stepTitle, subhead);
        add(stepSectionHeading);

        // Sequence of Step Cards
        int total = blueprint.stepCount();
        for (PromptStep step : blueprint.steps()) {
            StepCardComponent card = new StepCardComponent(step, total);
            card.setWidthFull();
            add(card);
        }

        // Action Trigger Button for Copying complete blueprints
        Button bulkCopyBtn = new Button("Export Full Blueprint Context Script");
        bulkCopyBtn.setWidthFull();
        bulkCopyBtn.getStyle()
            .set("background-color", "#faf8f5")
            .set("color", "#1e1c19")
            .set("border", "1px solid #ebd9c8")
            .set("border-radius", "12px")
            .set("font-size", "14px")
            .set("font-weight", "700")
            .set("padding", "16px")
            .set("margin-top", "24px")
            .set("cursor", "pointer")
            .set("transition", "all 0.15s ease");

        String structuralText = buildContextLogText(blueprint);

        bulkCopyBtn.addClickListener(e -> {
            bulkCopyBtn.getElement().executeJs(
                "navigator.clipboard.writeText($0).then(() => { " +
                "  $1.textContent = 'All steps copied!'; " +
                "  setTimeout(() => { $1.textContent = 'Export Full Blueprint Context Script'; }, 2000); " +
                "})",
                structuralText,
                bulkCopyBtn.getElement()
            );

            Notification note = Notification.show("Full structural pipeline copied to clipboard.", 2000, Notification.Position.BOTTOM_CENTER);
            note.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        add(bulkCopyBtn);
    }

    private String buildContextLogText(PromptBlueprint blueprint) {
        StringBuilder builder = new StringBuilder();
        builder.append("=========================================\n");
        builder.append(" TOKENBUDGET PLANNER PIPELINE \n");
        builder.append("=========================================\n\n");
        builder.append("Goal Context: ").append(blueprint.originalGoal()).append("\n");
        builder.append("Intent Category: ").append(blueprint.category().getDisplayName()).append("\n");
        builder.append("Estimated Savings: ").append(String.format("%.1f%%", blueprint.savingsPercentage())).append("\n\n");

        for (PromptStep step : blueprint.steps()) {
            builder.append("PHASE ").append(step.stepNumber()).append(": ").append(step.title()).append("\n");
            builder.append("-----------------------------------------\n");
            builder.append(step.getFormattedPrompt().replace("of N]", "of " + blueprint.stepCount() + "]")).append("\n\n");
        }
        return builder.toString();
    }
}