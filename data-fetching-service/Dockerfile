# --- Stage 1: Builder Stage ---
FROM openjdk:21-jdk-slim as builder

WORKDIR /app

# Copy the Gradle wrapper and its configuration directory
# Make sure you have the ./gradlew wrapper in your project root
# and the gradle/ directory.
COPY gradlew .
COPY gradle ./gradle/

# Copy the build files (build.gradle, settings.gradle)
# and the source code directories.
COPY build.gradle .
COPY settings.gradle .
COPY src ./src/

# Grant execution rights to the Gradle wrapper script
RUN chmod +x gradlew

# Run the Gradle build command to produce the executable JAR
# -x test is used to skip running tests during the Docker build for speed.
RUN ./gradlew clean build -x test

# --- Stage 2: Runner Stage ---
FROM openjdk:21-jdk-slim

# Set the working directory in the final image
WORKDIR /app

# Copy the JAR artifact from the builder stage to the runner stage
# Gradle typically places the executable JAR in build/libs/
# Remember to replace 'your-application-name.jar' with the actual name of your JAR file.
# Gradle usually names it [project-name]-[version].jar
COPY --from=builder /app/build/libs/data-fetching-service-0.jar /app/app.jar

# Expose the port the Spring Boot app runs on (default is 8080)
EXPOSE 8080

# Define the command to run the application using the copied JAR
# Use the exec form for proper signal handling
CMD ["java", "-jar", "/app/app.jar"]

