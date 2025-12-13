# ============================
# Build stage
# ============================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom first (better caching)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copy source
COPY src ./src

# Build jar
RUN mvn -B -DskipTests clean package


# ============================
# Runtime stage
# ============================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy built jar
COPY --from=build /app/target/*.jar app.jar

# Render provides PORT at runtime
EXPOSE 8080

# JVM optimizations for Render free tier
ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx512m -XX:+UseG1GC"

# Start app
ENTRYPOINT ["java", "-jar", "app.jar"]
