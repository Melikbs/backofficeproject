# Use a slim Java image
FROM openjdk:17-jdk-slim

# Set working dir
WORKDIR /app

# Copy built jar from local to container
COPY target/BackOfficeProject-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
