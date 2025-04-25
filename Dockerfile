FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .

RUN --mount=type=cache,id=maven-cache,target=/root/.m2/repository \
    mvn dependency:go-offline -B
COPY src ./src

RUN --mount=type=cache,id=maven-cache,target=/root/.m2/repository \
    mvn clean package -DskipTests -B

FROM openjdk:17-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target target
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]