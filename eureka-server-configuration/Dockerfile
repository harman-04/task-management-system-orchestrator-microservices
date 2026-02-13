FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY target/eureka-server-configuration-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java" , "-jar" , "app.jar"]