// src/main/java/com/tokenbudget/ui/components/TaskInputComponent.java
package com.tokenbudget.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.function.Consumer;

public class TaskInputComponent extends VerticalLayout {

    private final TextArea taskInput;
    private final Button generateButton;
    private final Span charCounter;

    public TaskInputComponent(Consumer<String> onGenerate) {
        setWidthFull();
        setPadding(false);
        setSpacing(false);

        Div containerCard = new Div();
        containerCard.setWidthFull();
        containerCard.getStyle()
            .set("background-color", "#ffffff")
            .set("border-radius", "16px")
            .set("border", "1px solid #ebd9c8")
            .set("padding", "32px")
            .set("box-shadow", "0 12px 40px -12px rgba(30, 28, 25, 0.05)");

        Paragraph promptLabel = new Paragraph("PROMPT GOAL");
        promptLabel.getStyle()
            .set("font-size", "11px")
            .set("font-weight", "800")
            .set("letter-spacing", "1px")
            .set("color", "#757069")
            .set("margin", "0 0 10px 0");

        taskInput = new TextArea();
        taskInput.setWidthFull();
        taskInput.setPlaceholder("What are we building? Describe your feature, bugfix, migration, or refactoring task in plain English...");
        taskInput.setMinHeight("110px");
        taskInput.setMaxHeight("180px");
        taskInput.getStyle()
            .set("--vaadin-input-field-background", "#faf8f5")
            .set("--vaadin-input-field-border-width", "1px")
            .set("--vaadin-input-field-border-color", "#e8e4db")
            .set("--vaadin-input-field-focus-ring-color", "#e05a2b")
            .set("border-radius", "10px")
            .set("font-size", "14px")
            .set("line-height", "1.6")
            .set("color", "#1e1c19");

        // Footer element containing characters counter and examples
        Div footerMeta = new Div();
        footerMeta.getStyle()
            .set("display", "flex")
            .set("justify-content", "space-between")
            .set("align-items", "center")
            .set("margin-top", "12px");

        // Quick template tag
        Span quickTag = new Span("QUICK TEMPLATES:");
        quickTag.getStyle()
            .set("font-size", "10px")
            .set("font-weight", "800")
            .set("color", "#a39e95")
            .set("margin-right", "8px");

        Div templatesWrapper = new Div();
        templatesWrapper.getStyle()
            .set("display", "flex")
            .set("gap", "6px")
            .set("flex-wrap", "wrap");
        templatesWrapper.add(quickTag);

        String[] quickExamples = {
            "Migrate MySQL to Postgres",
            "Build full JWT Auth system",
            "Refactor code monolith",
            "Integrate Stripe payments"
        };

        for (String example : quickExamples) {
            Button pill = new Button(example);
            pill.getStyle()
                .set("background-color", "#faf8f5")
                .set("color", "#5e5952")
                .set("border", "1px solid #ebd9c8")
                .set("border-radius", "6px")
                .set("font-size", "11px")
                .set("font-weight", "600")
                .set("padding", "2px 10px")
                .set("cursor", "pointer")
                .set("height", "auto")
                .set("transition", "all 0.15s ease");
            pill.addClickListener(e -> taskInput.setValue(example));
            templatesWrapper.add(pill);
        }

        charCounter = new Span("0 characters");
        charCounter.getStyle()
            .set("color", "#a39e95")
            .set("font-size", "11px")
            .set("font-weight", "600");

        taskInput.addValueChangeListener(e -> {
            int len = e.getValue() != null ? e.getValue().length() : 0;
            charCounter.setText(len + " / 2000");
        });

        footerMeta.add(templatesWrapper, charCounter);

        generateButton = new Button("Decompose & Estimate Prompt Steps");
        generateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        generateButton.setWidthFull();
        generateButton.getStyle()
            .set("background-color", "#1e1c19")
            .set("color", "#ffffff")
            .set("font-weight", "700")
            .set("font-size", "14px")
            .set("letter-spacing", "0.5px")
            .set("border-radius", "10px")
            .set("margin-top", "24px")
            .set("padding", "14px")
            .set("cursor", "pointer")
            .set("border", "none")
            .set("transition", "all 0.15s ease");

        generateButton.addClickListener(e -> {
            String val = taskInput.getValue();
            if (val != null && !val.isBlank()) {
                onGenerate.accept(val.strip());
            }
        });

        containerCard.add(promptLabel, taskInput, footerMeta, generateButton);
        add(containerCard);
    }

    public void setLoading(boolean loading) {
        generateButton.setEnabled(!loading);
        generateButton.setText(loading ? "Analysing context patterns..." : "Decompose & Estimate Prompt Steps");
        generateButton.getStyle()
            .set("background-color", loading ? "#757069" : "#1e1c19");
    }
}