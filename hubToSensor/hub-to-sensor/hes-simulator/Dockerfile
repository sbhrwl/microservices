# Stage 1: Build the JAR
FROM eclipse-temurin:17-jdk-focal AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline
COPY src /app/src
RUN ./mvnw clean install -DskipTests

# Stage 2: Create the final production image
FROM eclipse-temurin:17-jre-focal AS final
ARG JAR_FILE=target/hes-simulator-*.jar
COPY --from=build /app/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]