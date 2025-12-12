# ================================
# Build Stage
# ================================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom and download dependencies first (build cache friendly)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copy source
COPY src ./src

# Build jar
RUN mvn -B -q package -DskipTests

# ================================
# Run Stage
# ================================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy built JAR
COPY --from=build /app/target/*.jar app.jar

# Railway automatically injects $PORT
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
