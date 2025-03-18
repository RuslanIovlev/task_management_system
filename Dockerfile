FROM openjdk:17-jdk-slim-buster
WORKDIR /app

COPY build/libs/TaskManagementSystem-0.0.1-SNAPSHOT.jar /app/taskmanagement.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "taskmanagement.jar"]
