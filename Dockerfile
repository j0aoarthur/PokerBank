# ─── Estágio 1: Build ──────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar o cache de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copia o source e compila
COPY src ./src
RUN mvn clean package -DskipTests -q

# ─── Estágio 2: Runtime ────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Usuario nao-root por seguranca
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/target/pokerbank.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]