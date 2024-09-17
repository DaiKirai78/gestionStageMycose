FROM openjdk:21-jdk-slim

COPY target/mycose-0.0.1-SNAPSHOT.jar /app/mycose.jar

WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "mycose.jar"]