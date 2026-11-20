# ==============================================================================
# STAGE 1: Build Spring Boot JAR with JDK 21
# ==============================================================================
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

COPY mvnw pom.xml ./
COPY .mvn ./.mvn
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ==============================================================================
# STAGE 2: Lightweight JRE 21 Runtime
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN mkdir -p /app/uploads

COPY --from=builder /build/target/*.jar /app/app.jar

ENV APP_UPLOAD_DIR=/app/uploads

VOLUME ["/app/uploads"]
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
