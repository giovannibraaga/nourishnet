FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY . .
RUN mvn -q -DskipTests package spring-boot:repackage

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN useradd -r -s /bin/false appuser && chown appuser:appuser /app
COPY --from=build /build/target/*.jar /app/app.jar

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=60.0 -XX:MaxMetaspaceSize=128m"
ENV SERVER_PORT=8080
EXPOSE 8080

USER appuser
ENTRYPOINT ["java","-jar","/app/app.jar"]
