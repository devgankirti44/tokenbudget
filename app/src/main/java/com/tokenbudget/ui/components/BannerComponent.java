// src/main/java/com/tokenbudget/ui/components/BannerComponent.java
package com.tokenbudget.ui.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class BannerComponent extends VerticalLayout {

    public BannerComponent() {
        setWidthFull();
        setAlignItems(Alignment.CENTER);
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("padding", "60px 0 40px 0")
            .set("font-family", "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif");

        // Small premium badge
        Span versionBadge = new Span("TOKENBUDGET ENGINE V1.0");
        versionBadge.getStyle()
            .set("font-size", "11px")
            .set("font-weight", "700")
            .set("letter-spacing", "1.5px")
            .set("color", "#e05a2b") // Premium Burnt Terracotta
            .set("background-color", "#fcf1ec")
            .set("padding", "4px 12px")
            .set("border-radius", "100px")
            .set("border", "1px solid #f3d4c7")
            .set("margin-bottom", "16px");

        // Main Premium Title
        H1 title = new H1();
        Span tokenPart = new Span("Token");
        tokenPart.getStyle()
            .set("color", "#1e1c19")
            .set("font-weight", "800");
        Span budgetPart = new Span("Budget");
        budgetPart.getStyle()
            .set("color", "#e05a2b")
            .set("font-weight", "300"); // Elegant modern styling dynamic

        title.add(tokenPart, budgetPart);
        title.getStyle()
            .set("font-size", "48px")
            .set("margin", "0")
            .set("letter-spacing", "-1.5px")
            .set("line-height", "1.1");

        // Tagline
        Paragraph tagline = new Paragraph("Plan your prompts. Save your tokens.");
        tagline.getStyle()
            .set("color", "#757069")
            .set("font-size", "18px")
            .set("font-weight", "400")
            .set("margin-top", "8px")
            .set("margin-bottom", "12px");

        // Minimalist divider
        Div divider = new Div();
        divider.getStyle()
            .set("width", "40px")
            .set("height", "2px")
            .set("background-color", "#e8e4db")
            .set("margin-bottom", "16px");

        add(versionBadge, title, tagline, divider);
    }
}