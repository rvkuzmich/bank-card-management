FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src src

RUN mvn dependency:go-offline -B

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]