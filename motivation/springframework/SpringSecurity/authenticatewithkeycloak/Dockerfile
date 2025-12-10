# Stage 1: Build the JAR
FROM eclipse-temurin:17-jdk-focal AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Copy the Spring Boot jar (SNAPSHOT name)
COPY --from=build /app/target/*SNAPSHOT.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
