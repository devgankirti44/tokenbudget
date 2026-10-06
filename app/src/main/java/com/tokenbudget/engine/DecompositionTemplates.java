// src/main/java/com/tokenbudget/engine/DecompositionTemplates.java
package com.tokenbudget.engine;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class DecompositionTemplates {

    public record StepTemplate(
        String title,
        String descriptionTemplate,
        String promptTemplate,
        String focusArea,
        int baseTokenEstimate
    ) {}

    private static final Map<TaskCategory, List<StepTemplate>> TEMPLATES = new LinkedHashMap<>();

    static {
        TEMPLATES.put(TaskCategory.DATABASE_MIGRATION, List.of(
            new StepTemplate(
                "Dependencies & Configuration",
                "Update build dependencies and configure the new database connection.",
                "I am migrating my application's database layer. For this first step, I need you to:\n" +
                "1. List the exact Maven/Gradle dependencies to ADD for the target database.\n" +
                "2. List the dependencies to REMOVE from the old database.\n" +
                "3. Provide the updated application.properties/yml configuration for the new database connection.\n" +
                "Goal context: %s\n" +
                "Only address dependencies and configuration. Do not modify any Java code yet.",
                "Build config & datasource properties",
                250
            ),
            new StepTemplate(
                "Schema & Entity Model Conversion",
                "Convert entity models, column types, and schema definitions to the target database dialect.",
                "Continuing the database migration. The dependencies and configuration are now updated.\n" +
                "For this step, I need you to:\n" +
                "1. Identify all JPA entity annotations that need dialect-specific changes.\n" +
                "2. Convert any database-specific column types (e.g., MySQL AUTO_INCREMENT → PostgreSQL SERIAL).\n" +
                "3. Update any native SQL in @Query annotations to the target dialect.\n" +
                "4. Provide the updated entity classes.\n" +
                "Goal context: %s\n" +
                "Only address entity/model changes. Do not touch service or controller layers.",
                "Entity models & schema DDL",
                450
            ),
            new StepTemplate(
                "Repository & Data Access Layer",
                "Update repository interfaces, custom queries, and data access patterns.",
                "Continuing the migration. Entities are now converted.\n" +
                "For this step:\n" +
                "1. Update all repository interfaces with any dialect-specific query changes.\n" +
                "2. Convert any raw JDBC or native queries to the target database syntax.\n" +
                "3. Update any stored procedure calls or database function references.\n" +
                "4. Verify pagination and sorting compatibility.\n" +
                "Goal context: %s\n" +
                "Only address repository and data access code.",
                "Repositories & query syntax",
                400
            ),
            new StepTemplate(
                "Data Migration Script",
                "Create a data migration strategy and script for existing data.",
                "The code migration is complete. Now I need a data migration strategy.\n" +
                "For this step:\n" +
                "1. Provide a SQL script or migration tool config to transfer existing data.\n" +
                "2. Handle any type conversion edge cases between source and target databases.\n" +
                "3. Include rollback instructions if the migration fails.\n" +
                "Goal context: %s\n" +
                "Only address data migration, not code changes.",
                "Data transfer & rollback",
                350
            ),
            new StepTemplate(
                "Verification & Smoke Tests",
                "Validate the migration with targeted tests and a verification checklist.",
                "The migration code and data transfer are complete.\n" +
                "For this final step:\n" +
                "1. Provide a smoke test checklist for verifying the migration.\n" +
                "2. Write 2-3 integration test methods that verify critical database operations.\n" +
                "3. List common pitfalls to watch for post-migration.\n" +
                "Goal context: %s\n" +
                "Focus only on verification and testing.",
                "Integration tests & validation",
                300
            )
        ));

        TEMPLATES.put(TaskCategory.FEATURE_DEVELOPMENT, List.of(
            new StepTemplate(
                "Requirements & Data Model",
                "Define the data model, entities, and database schema for the new feature.",
                "I am building a new feature for my application. For this first step:\n" +
                "1. Based on the goal below, define the necessary JPA entities or data models.\n" +
                "2. Include all fields, relationships, and validation annotations.\n" +
                "3. Provide any database migration/schema SQL if needed.\n" +
                "Goal: %s\n" +
                "Only create the data model layer. Do not implement any business logic yet.",
                "Entities, DTOs & schema",
                400
            ),
            new StepTemplate(
                "Service & Business Logic",
                "Implement the core business logic and service layer for the feature.",
                "The data model for this feature is now defined. For this step:\n" +
                "1. Create the service class(es) with all business logic methods.\n" +
                "2. Include input validation, error handling, and edge case management.\n" +
                "3. Define any custom exceptions needed.\n" +
                "Goal: %s\n" +
                "Only implement the service layer. Do not create controllers or UI yet.",
                "Service classes & validation",
                500
            ),
            new StepTemplate(
                "API / Controller Layer",
                "Create REST endpoints or controller methods to expose the feature.",
                "The service layer is implemented. For this step:\n" +
                "1. Create the REST controller or MVC controller with appropriate endpoints.\n" +
                "2. Map request/response DTOs properly.\n" +
                "3. Add appropriate HTTP status codes, error responses, and API documentation annotations.\n" +
                "Goal: %s\n" +
                "Only create the controller/API layer.",
                "REST endpoints & DTOs",
                450
            ),
            new StepTemplate(
                "Frontend / UI Integration",
                "Build the user interface components that interact with the new endpoints.",
                "The backend API is complete. For this step:\n" +
                "1. Create the UI components/views needed for this feature.\n" +
                "2. Connect UI to the backend endpoints.\n" +
                "3. Add user input validation and error display on the frontend.\n" +
                "Goal: %s\n" +
                "Only address UI/frontend work.",
                "Views, forms & API binding",
                500
            ),
            new StepTemplate(
                "Testing & Edge Cases",
                "Write tests and handle edge cases for the complete feature.",
                "The feature is fully implemented end-to-end. For this final step:\n" +
                "1. Write unit tests for the service layer (at least 3 test methods).\n" +
                "2. Write one integration test for the API endpoint.\n" +
                "3. List edge cases that should be manually tested.\n" +
                "Goal: %s\n" +
                "Focus only on testing and quality assurance.",
                "Unit tests & edge cases",
                350
            )
        ));

        TEMPLATES.put(TaskCategory.DEBUGGING_BUGFIX, List.of(
            new StepTemplate(
                "Reproduce & Isolate",
                "Reproduce the bug reliably and isolate it to a specific component.",
                "I need to fix a bug in my application. For this first step:\n" +
                "1. Help me create a minimal reproduction case for this issue.\n" +
                "2. Identify which layer (data, service, controller, UI) is most likely the source.\n" +
                "3. Suggest specific log statements or debug breakpoints to add.\n" +
                "Bug description: %s\n" +
                "Only help with reproduction and isolation. Do not suggest fixes yet.",
                "Reproduction & root cause isolation",
                300
            ),
            new StepTemplate(
                "Root Cause Analysis",
                "Analyze the isolated component to identify the exact root cause.",
                "I've isolated the bug to a specific area. For this step:\n" +
                "1. Analyze the most likely root causes given the symptoms.\n" +
                "2. Explain the expected behavior vs. actual behavior.\n" +
                "3. Identify if this is a logic error, data issue, race condition, or configuration problem.\n" +
                "Bug description: %s\n" +
                "Only analyze the cause. Do not write the fix code yet.",
                "Diagnosis & cause identification",
                350
            ),
            new StepTemplate(
                "Implement Fix",
                "Write the targeted code fix for the identified root cause.",
                "The root cause is identified. For this step:\n" +
                "1. Provide the minimal code change that fixes the root cause.\n" +
                "2. Explain why this fix addresses the root cause.\n" +
                "3. Note any side effects this change might have.\n" +
                "Bug description: %s\n" +
                "Provide only the fix. Do not add new features or refactor unrelated code.",
                "Targeted code fix",
                400
            ),
            new StepTemplate(
                "Regression Test",
                "Write a test that catches this specific bug to prevent regression.",
                "The fix is applied. For this final step:\n" +
                "1. Write a unit test that would have caught this bug.\n" +
                "2. Write a test that verifies the fix works correctly.\n" +
                "3. Suggest any related areas that should be tested for similar issues.\n" +
                "Bug description: %s\n" +
                "Only address testing and regression prevention.",
                "Regression test & prevention",
                300
            )
        ));

        TEMPLATES.put(TaskCategory.CODE_REFACTORING, List.of(
            new StepTemplate(
                "Code Audit & Smell Identification",
                "Identify the specific code smells, anti-patterns, or structural issues to address.",
                "I want to refactor part of my codebase. For this first step:\n" +
                "1. Identify the key code smells or structural issues related to my goal.\n" +
                "2. Categorize each issue (duplication, long methods, tight coupling, etc.).\n" +
                "3. Prioritize which issues to address first.\n" +
                "Refactoring goal: %s\n" +
                "Only identify and prioritize issues. Do not write refactored code yet.",
                "Code smell audit & prioritization",
                300
            ),
            new StepTemplate(
                "Design Target Architecture",
                "Define the target structure and design patterns to apply.",
                "Issues are identified. For this step:\n" +
                "1. Propose the target class/method structure after refactoring.\n" +
                "2. Identify any design patterns that should be applied.\n" +
                "3. Define the new interfaces or abstractions needed.\n" +
                "Refactoring goal: %s\n" +
                "Only describe the target architecture. Do not implement yet.",
                "Target structure & patterns",
                350
            ),
            new StepTemplate(
                "Implement Refactoring",
                "Execute the refactoring, moving code to match the target architecture.",
                "The target architecture is defined. For this step:\n" +
                "1. Refactor the code to match the target architecture.\n" +
                "2. Ensure all existing behavior is preserved (no functional changes).\n" +
                "3. Apply proper naming conventions and documentation.\n" +
                "Refactoring goal: %s\n" +
                "Only implement the refactoring. No new features.",
                "Code restructuring",
                500
            ),
            new StepTemplate(
                "Verify Behavior Preservation",
                "Confirm that refactoring preserved all existing functionality.",
                "Refactoring is complete. For this final step:\n" +
                "1. Provide before/after comparison of key method signatures.\n" +
                "2. List all tests that should be re-run to verify behavior preservation.\n" +
                "3. Write any new tests needed for the refactored structure.\n" +
                "Refactoring goal: %s\n" +
                "Focus only on verification.",
                "Test verification & comparison",
                300
            )
        ));

        TEMPLATES.put(TaskCategory.API_INTEGRATION, List.of(
            new StepTemplate(
                "API Research & Contract Definition",
                "Understand the external API's authentication, endpoints, and data contracts.",
                "I need to integrate with an external API. For this first step:\n" +
                "1. Define the authentication method needed (API key, OAuth, Bearer token, etc.).\n" +
                "2. List the specific endpoints I'll need to call for my use case.\n" +
                "3. Define the request/response DTOs based on the API's data contract.\n" +
                "Integration goal: %s\n" +
                "Only address API contract research and DTO creation.",
                "API contracts & DTOs",
                350
            ),
            new StepTemplate(
                "HTTP Client & Service Layer",
                "Build the HTTP client configuration and service wrapper for API calls.",
                "The API contract is defined. For this step:\n" +
                "1. Configure the HTTP client (RestTemplate, WebClient, or HttpClient).\n" +
                "2. Create a service class that wraps each API call.\n" +
                "3. Implement proper error handling for HTTP errors and timeouts.\n" +
                "Integration goal: %s\n" +
                "Only build the client and service layer.",
                "HTTP client & error handling",
                450
            ),
            new StepTemplate(
                "Integration Wiring & Data Mapping",
                "Wire the API service into your application and map data between systems.",
                "The API client service is built. For this step:\n" +
                "1. Wire the API service into existing application services.\n" +
                "2. Map external API data to internal domain models.\n" +
                "3. Handle any data transformation or enrichment logic.\n" +
                "Integration goal: %s\n" +
                "Only address integration wiring and mapping.",
                "Data mapping & wiring",
                400
            ),
            new StepTemplate(
                "Testing with Mocks",
                "Test the integration using mock responses to verify correctness without live API calls.",
                "The integration is wired. For this final step:\n" +
                "1. Create mock API responses for testing.\n" +
                "2. Write tests that verify correct behavior with mock data.\n" +
                "3. Add a simple health check or connectivity test for the live API.\n" +
                "Integration goal: %s\n" +
                "Focus only on testing.",
                "Mock tests & health checks",
                300
            )
        ));

        TEMPLATES.put(TaskCategory.TESTING_QA, List.of(
            new StepTemplate(
                "Test Strategy & Framework Setup",
                "Define testing strategy and configure the testing framework.",
                "I need to set up testing for my application. For this first step:\n" +
                "1. Recommend the appropriate testing frameworks and dependencies.\n" +
                "2. Provide the Maven/Gradle configuration for the test dependencies.\n" +
                "3. Create a base test configuration class if needed.\n" +
                "Testing goal: %s\n" +
                "Only address setup and configuration.",
                "Framework config & strategy",
                300
            ),
            new StepTemplate(
                "Unit Test Implementation",
                "Write unit tests for core business logic with proper mocking.",
                "The test framework is configured. For this step:\n" +
                "1. Write unit tests for the most critical service methods.\n" +
                "2. Use proper mocking for dependencies.\n" +
                "3. Cover happy path, edge cases, and error scenarios.\n" +
                "Testing goal: %s\n" +
                "Only write unit tests.",
                "Unit tests & mocking",
                450
            ),
            new StepTemplate(
                "Integration Test Implementation",
                "Write integration tests that verify component interaction.",
                "Unit tests are written. For this step:\n" +
                "1. Write integration tests for API endpoints or database operations.\n" +
                "2. Configure test database or embedded database if needed.\n" +
                "3. Verify end-to-end request/response flows.\n" +
                "Testing goal: %s\n" +
                "Only write integration tests.",
                "Integration tests & test DB",
                400
            )
        ));

        TEMPLATES.put(TaskCategory.DEPLOYMENT_DEVOPS, List.of(
            new StepTemplate(
                "Build & Package Configuration",
                "Configure the build process for production deployment.",
                "I need to deploy my application. For this first step:\n" +
                "1. Configure the production build profile.\n" +
                "2. Set up environment-specific configuration (dev/staging/prod).\n" +
                "3. Create any necessary build scripts.\n" +
                "Deployment goal: %s\n" +
                "Only address build configuration.",
                "Build profiles & packaging",
                300
            ),
            new StepTemplate(
                "Containerization or Platform Config",
                "Create Docker configuration or platform-specific deployment files.",
                "Build configuration is complete. For this step:\n" +
                "1. Create a Dockerfile or platform-specific config (render.yaml, etc.).\n" +
                "2. Configure health checks and resource limits.\n" +
                "3. Set up environment variable management.\n" +
                "Deployment goal: %s\n" +
                "Only address containerization or platform config.",
                "Docker / platform config",
                350
            ),
            new StepTemplate(
                "CI/CD Pipeline",
                "Set up automated build, test, and deployment pipeline.",
                "Platform configuration is ready. For this step:\n" +
                "1. Create CI/CD pipeline configuration (GitHub Actions, etc.).\n" +
                "2. Include build, test, and deploy stages.\n" +
                "3. Configure automated deployment triggers.\n" +
                "Deployment goal: %s\n" +
                "Only address CI/CD pipeline.",
                "Pipeline & automation",
                350
            ),
            new StepTemplate(
                "Monitoring & Validation",
                "Set up monitoring and validate the deployment.",
                "Deployment pipeline is configured. For this final step:\n" +
                "1. Add application health endpoints.\n" +
                "2. Configure logging for production.\n" +
                "3. Provide a post-deployment validation checklist.\n" +
                "Deployment goal: %s\n" +
                "Focus on monitoring and validation only.",
                "Health checks & logging",
                250
            )
        ));

        TEMPLATES.put(TaskCategory.GENERAL_TECHNICAL_TASK, List.of(
            new StepTemplate(
                "Requirements Analysis",
                "Break down the goal into clear, specific technical requirements.",
                "I have a technical task to complete. For this first step:\n" +
                "1. Break down my goal into specific, actionable technical requirements.\n" +
                "2. Identify any prerequisites or dependencies.\n" +
                "3. Flag any ambiguities that need clarification.\n" +
                "Task goal: %s\n" +
                "Only analyze requirements. Do not implement anything yet.",
                "Requirements & prerequisites",
                300
            ),
            new StepTemplate(
                "Core Implementation",
                "Implement the primary logic and functionality.",
                "Requirements are defined. For this step:\n" +
                "1. Implement the core logic for the primary requirement.\n" +
                "2. Follow established patterns and best practices.\n" +
                "3. Include error handling and input validation.\n" +
                "Task goal: %s\n" +
                "Only implement the core functionality.",
                "Primary implementation",
                500
            ),
            new StepTemplate(
                "Integration & Wiring",
                "Connect the implementation to the rest of the application.",
                "Core logic is implemented. For this step:\n" +
                "1. Wire the new code into existing application components.\n" +
                "2. Update any configuration files needed.\n" +
                "3. Verify compatibility with existing code.\n" +
                "Task goal: %s\n" +
                "Only address integration and wiring.",
                "Wiring & configuration",
                350
            ),
            new StepTemplate(
                "Testing & Documentation",
                "Test the implementation and add documentation.",
                "Implementation is wired in. For this final step:\n" +
                "1. Write at least 2 test methods for the new functionality.\n" +
                "2. Add code comments and any necessary README documentation.\n" +
                "3. Provide a verification checklist.\n" +
                "Task goal: %s\n" +
                "Focus only on testing and documentation.",
                "Tests & documentation",
                300
            )
        ));
    }

    public List<StepTemplate> getTemplatesForCategory(TaskCategory category) {
        return TEMPLATES.getOrDefault(category, TEMPLATES.get(TaskCategory.GENERAL_TECHNICAL_TASK));
    }

    public List<PromptStep> generateSteps(TaskCategory category, String originalGoal) {
        List<StepTemplate> templates = getTemplatesForCategory(category);
        List<PromptStep> steps = new ArrayList<>();

        for (int i = 0; i < templates.size(); i++) {
            StepTemplate tmpl = templates.get(i);
            String filledPrompt = String.format(tmpl.promptTemplate(), originalGoal);
            steps.add(new PromptStep(
                i + 1,
                tmpl.title(),
                tmpl.descriptionTemplate(),
                filledPrompt,
                tmpl.baseTokenEstimate(),
                tmpl.focusArea()
            ));
        }
        return steps;
    }
}