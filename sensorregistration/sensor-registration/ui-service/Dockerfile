FROM openjdk:17-jdk-slim

# Install netcat for debugging
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]