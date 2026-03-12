# ============================================
# QuickPoll Backend — Multi-stage Build
# Java 21 / Spring Boot 3.2 / Maven
# ============================================
# syntax=docker/dockerfile:1

# Build Args
ARG MAVEN_VERSION=3.9.6
ARG JDK_VERSION=21

# Stage 1: Dependencies (cached layer)
FROM maven:${MAVEN_VERSION}-eclipse-temurin-${JDK_VERSION}-alpine AS deps
WORKDIR /build
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn dependency:go-offline -B -q

# Stage 2: Build
FROM deps AS build
WORKDIR /build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn clean package -DskipTests -B -q \
    && mv target/*.jar target/app.jar

# Stage 3: Runtime
FROM eclipse-temurin:${JDK_VERSION}-jre-alpine AS runtime

# OCI image labels
LABEL org.opencontainers.image.title="QuickPoll Backend" \
    org.opencontainers.image.description="Real-Time Polling Platform API — Spring Boot 3.2" \
    org.opencontainers.image.source="https://github.com/QuickPoll-app/QuickPoll"

WORKDIR /app

# Security: non-root user + health check dependency (single layer)
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup && \
    apk add --no-cache curl tzdata && \
    rm -rf /var/cache/apk/*

# Copy built artifact
COPY --from=build --chown=appuser:appgroup /build/target/app.jar app.jar

# Drop to non-root
USER appuser:appgroup

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -sf http://localhost:8081/actuator/health || exit 1

# Graceful shutdown + container-aware JVM
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-Dspring.lifecycle.timeout-per-shutdown-phase=30s", \
    "-jar", "app.jar"]
