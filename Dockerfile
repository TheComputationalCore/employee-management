# =========================
# BUILD STAGE
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# IMPORTANT: limit memory for Render free tier
ENV MAVEN_OPTS="-Xms128m -Xmx384m -XX:+UseG1GC"

# Copy pom.xml first (layer caching)
COPY pom.xml .

# Download dependencies only
RUN mvn -B dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN mvn -B -DskipTests clean package


# =========================
# RUNTIME STAGE
# =========================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy built JAR
COPY --from=build /app/target/*.jar app.jar

# Render injects PORT dynamically
EXPOSE 8080

# JVM tuning (Render-safe)
ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx384m -XX:+UseG1GC -XX:MaxMetaspaceSize=128m"

# IMPORTANT: no shell, no sh - keeps signals clean
ENTRYPOINT ["java","-jar","/app/app.jar"]
