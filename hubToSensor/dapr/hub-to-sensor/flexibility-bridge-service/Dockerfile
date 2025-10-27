# Stage 1: Build the JAR
FROM eclipse-temurin:17-jdk-focal AS build
WORKDIR /app

# Copy Maven Wrapper files
COPY mvnw .
COPY .mvn .mvn

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Copy source code and build
COPY src /app/src
RUN ./mvnw clean install -DskipTests

# Stage 2: Create the final production image
FROM eclipse-temurin:17-jre-focal AS final

# Define the JAR file name based on the service name
ARG JAR_FILE=target/flexibility-bridge-*.jar

# Copy the built JAR from the first stage
COPY --from=build /app/${JAR_FILE} app.jar

# Define the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]