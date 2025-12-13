# =========================
# Build stage
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Reduce Maven memory usage (CRITICAL for Render free tier)
ENV MAVEN_OPTS="-Xms128m -Xmx384m -XX:+UseG1GC"

# Copy pom first (dependency caching)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copy source
COPY src ./src

# Build JAR
RUN mvn -B -DskipTests package

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy only the JAR (small final image)
COPY --from=build /app/target/*.jar app.jar

# Render provides PORT
EXPOSE 8080

# JVM tuning for Render free tier
ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx384m -XX:+UseG1GC -XX:MaxMetaspaceSize=128m"

ENTRYPOINT ["java", "-jar", "app.jar"]
