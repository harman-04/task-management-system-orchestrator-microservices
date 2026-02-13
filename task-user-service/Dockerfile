FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY target/task-user-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java" , "-jar", "app.jar"]