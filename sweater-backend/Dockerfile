FROM maven:3.8.3-openjdk-17-slim AS build
COPY src /app/src
COPY pom.xml /app
RUN mvn -f /app/pom.xml clean package -DskipTests

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar sweater_app.jar
COPY src/main/resources/init_data src/main/resources/init_data
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "sweater_app.jar"]
