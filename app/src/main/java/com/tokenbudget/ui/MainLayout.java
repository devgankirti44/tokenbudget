// src/main/java/com/tokenbudget/ui/MainLayout.java
package com.tokenbudget.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class MainLayout extends AppLayout {

    public MainLayout() {
        // Set the full page background color
        getElement().getStyle()
            .set("background-color", "#f7f4ee")
            .set("min-height", "100vh");

        // Remove default Vaadin padding/margins
        setPrimarySection(Section.NAVBAR);
    }
}