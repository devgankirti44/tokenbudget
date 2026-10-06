// src/main/java/com/tokenbudget/ui/views/PlannerView.java
package com.tokenbudget.ui.views;

import com.tokenbudget.engine.PromptBlueprint;
import com.tokenbudget.engine.TaskPlannerEngine;
import com.tokenbudget.ui.components.BannerComponent;
import com.tokenbudget.ui.components.BlueprintResultComponent;
import com.tokenbudget.ui.components.TaskInputComponent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("TokenBudget — Plan your prompts. Save your tokens.")
public class PlannerView extends VerticalLayout {

    private final TaskPlannerEngine plannerEngine;
    private final Div resultContainer;
    private final TaskInputComponent taskInput;

    public PlannerView(TaskPlannerEngine plannerEngine) {
        this.plannerEngine = plannerEngine;

        // Force a beautiful editorial background environment on the main layout
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setPadding(true);
        setSpacing(false);
        getStyle()
            .set("background-color", "#faf8f5") // Warm Premium Cream (Notion / Linear style)
            .set("min-height", "100vh")
            .set("padding-bottom", "80px");

        VerticalLayout maxContainer = new VerticalLayout();
        maxContainer.setMaxWidth("920px");
        maxContainer.setWidthFull();
        maxContainer.setPadding(false);
        maxContainer.setSpacing(false);

        // Core visual components
        BannerComponent banner = new BannerComponent();
        maxContainer.add(banner);

        taskInput = new TaskInputComponent(this::onGenerateBlueprint);
        maxContainer.add(taskInput);

        resultContainer = new Div();
        resultContainer.setWidthFull();
        resultContainer.getStyle()
            .set("margin-top", "32px");
        maxContainer.add(resultContainer);

        // Premium minimal footer branding
        Paragraph footerText = new Paragraph("TOKENBUDGET ENGINE • 100% SECURE • PRIVACY ENCRYPTED");
        footerText.getStyle()
            .set("font-size", "10px")
            .set("font-weight", "800")
            .set("letter-spacing", "2px")
            .set("color", "#a39e95")
            .set("margin-top", "60px")
            .set("text-align", "center");
        maxContainer.add(footerText);

        add(maxContainer);
    }

    private void onGenerateBlueprint(String promptText) {
        taskInput.setLoading(true);
        resultContainer.removeAll();

        try {
            if (promptText.length() < 10) {
                Notification.show("Please enter a more descriptive task goal (at least 10 chars).", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                taskInput.setLoading(false);
                return;
            }

            PromptBlueprint blueprint = plannerEngine.generateBlueprint(promptText);

            BlueprintResultComponent resultComp = new BlueprintResultComponent(blueprint);
            resultContainer.add(resultComp);

            resultContainer.getElement().executeJs(
                "this.scrollIntoView({behavior: 'smooth', block: 'start'})"
            );

        } catch (Exception err) {
            Notification.show("Generation failure: " + err.getMessage(), 4000, Notification.Position.BOTTOM_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } finally {
            taskInput.setLoading(false);
        }
    }
}