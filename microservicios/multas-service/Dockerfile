FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn install -N -DskipTests -q
COPY biblioteca-commons/pom.xml biblioteca-commons/
COPY biblioteca-commons/src biblioteca-commons/src
RUN mvn install -f biblioteca-commons/pom.xml -DskipTests -q
COPY multas-service/pom.xml multas-service/
COPY multas-service/src multas-service/src
RUN mvn clean package -f multas-service/pom.xml -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/multas-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
