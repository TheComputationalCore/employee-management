# =========================
# BUILD STAGE
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

ENV MAVEN_OPTS="-Xms128m -Xmx384m -XX:+UseG1GC"

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package


# =========================
# RUNTIME STAGE
# =========================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system --gid 10001 app \
    && useradd --system --uid 10001 --gid app --home-dir /app app \
    && mkdir -p /app/uploads \
    && chown -R app:app /app

COPY --from=build --chown=app:app /app/target/employee-management-2.0.0.jar app.jar

ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx384m -XX:+UseG1GC -XX:MaxMetaspaceSize=128m"

USER app

ENTRYPOINT ["java","-jar","/app/app.jar"]
