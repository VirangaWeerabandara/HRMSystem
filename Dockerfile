# Use a Java 17 base image (adjust if needed)
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the built jar into the container
COPY build/libs/HRMSystem-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the default port (adjust if needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]