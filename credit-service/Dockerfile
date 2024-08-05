FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests
RUN mvn package
FROM openjdk:17 AS runtime
WORKDIR /app
COPY --from=build /app/target/credit-service-0.0.1.jar .
CMD ["java", "-jar", "credit-service-0.0.1.jar"]
