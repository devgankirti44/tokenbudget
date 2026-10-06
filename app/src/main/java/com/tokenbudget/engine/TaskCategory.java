package com.tokenbudget.engine;

public enum TaskCategory {
    DATABASE_MIGRATION(
        "Database Migration",
        "Migrate, convert, or switch database systems, schemas, or ORMs.",
        "🗄️"
    ),
    FEATURE_DEVELOPMENT(
        "Feature Development",
        "Build a new feature, endpoint, page, or full-stack capability.",
        "🚀"
    ),
    DEBUGGING_BUGFIX(
        "Debugging & Bug Fix",
        "Diagnose, troubleshoot, or fix an existing defect or error.",
        "🐛"
    ),
    CODE_REFACTORING(
        "Code Refactoring",
        "Restructure, optimize, or clean up existing code without changing behavior.",
        "♻️"
    ),
    API_INTEGRATION(
        "API Integration",
        "Integrate with external APIs, SDKs, or third-party services.",
        "🔌"
    ),
    TESTING_QA(
        "Testing & QA",
        "Write unit tests, integration tests, or set up testing frameworks.",
        "🧪"
    ),
    DEPLOYMENT_DEVOPS(
        "Deployment & DevOps",
        "Configure CI/CD, containerization, hosting, or infrastructure.",
        "📦"
    ),
    GENERAL_TECHNICAL_TASK(
        "General Technical Task",
        "A technical task that doesn't fit neatly into other categories.",
        "⚙️"
    );

    private final String displayName;
    private final String description;
    private final String emoji;

    TaskCategory(String displayName, String description, String emoji) {
        this.displayName = displayName;
        this.description = description;
        this.emoji = emoji;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getEmoji() { return emoji; }
}