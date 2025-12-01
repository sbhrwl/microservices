# Stage 1: Build the JAR
FROM eclipse-temurin:17-jdk-focal AS build
WORKDIR /app
# Assuming Maven structure
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline
COPY src /app/src
RUN ./mvnw clean install -DskipTests

# Stage 2: Create the final production image
FROM eclipse-temurin:17-jre-focal AS final
# IMPORTANT: Adjust the JAR_FILE name if your artifact ID is different
ARG JAR_FILE=target/flexibility-hub-simulator-*.jar 
COPY --from=build /app/${JAR_FILE} app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
