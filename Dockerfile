FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .

RUN --mount=type=cache,id=maven-cache,target=/root/.m2/repository \
    mvn dependency:go-offline -B
COPY src ./src

RUN --mount=type=cache,id=maven-cache,target=/root/.m2/repository \
    mvn clean package -DskipTests -B

FROM openjdk:17-slim

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]