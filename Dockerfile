FROM maven:3.8.4-openjdk-17 as build

COPY src /app/src
COPY pom.xml /app

WORKDIR /app
RUN mvn clean package -DskipTests

FROM openjdk:17

COPY --from=build /app/target/*.jar /app/app.jar

WORKDIR /app
EXPOSE 8080

ENTRYPOINT java -jar app.jar