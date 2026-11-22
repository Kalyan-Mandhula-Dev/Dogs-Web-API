# Stage 1: Build the JAR using Maven
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy

RUN useradd --system --uid 10001 --no-create-home app

WORKDIR /app

COPY --from=build /app/target/dogs-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

USER app

ENTRYPOINT ["java", "-jar", "app.jar"]