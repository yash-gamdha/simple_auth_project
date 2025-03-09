FROM gradle:8.12.1-jdk23 AS build

WORKDIR /app

COPY build.gradle .
RUN gradle --refresh-dependencies

COPY src ./src
RUN gradle clean build -x test

FROM openjdk:23-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/simple_auth_project-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/simple_auth_project-0.0.1-SNAPSHOT.jar"]