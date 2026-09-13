# Stage 1: Build JAR using Maven and Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy Maven descriptor and fetch dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build production JAR (skipping tests for fast container builds)
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Java 21 Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S keystone && adduser -S keystone -G keystone
USER keystone

# Copy compiled JAR artifact
COPY --from=builder /build/target/KEYSTONE-*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
