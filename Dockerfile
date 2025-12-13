# ===============================
# Employee Management System
# Render Free Tier – Java 17
# ===============================

FROM eclipse-temurin:17-jre

# App directory
WORKDIR /app

# Copy pre-built Spring Boot JAR
# IMPORTANT: JAR must already exist in target/
COPY target/*.jar app.jar

# Render injects PORT at runtime
EXPOSE 8080

# JVM tuning for Render Free tier (512MB RAM)
ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx384m -XX:+UseG1GC -XX:MaxMetaspaceSize=128m -XX:+UseStringDeduplication"

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]
