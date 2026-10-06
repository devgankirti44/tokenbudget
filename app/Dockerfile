# Stage 1: Build application with Java 21 & Maven
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy pom.xml and source
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apt-get update && apt-get install -y maven

# Build production JAR with Vaadin frontend bundle
RUN mvn clean package -Pproduction -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy compiled jar from build stage
COPY --from=build /app/target/tokenbudget-1.0.0.jar app.jar

# Expose default port
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]