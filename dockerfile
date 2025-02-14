FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/smatech-authentication-service-0.0.1-SNAPSHOT.jar auth-service.jar


ENTRYPOINT ["java", "-jar", "auth-service.jar"]
