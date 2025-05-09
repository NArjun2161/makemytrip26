# Dockerfile
FROM openjdk:21-jdk-slim
COPY target/makemytrip-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app.jar"]
