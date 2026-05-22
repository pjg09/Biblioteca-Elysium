FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn install -N -DskipTests -q
COPY biblioteca-commons/pom.xml biblioteca-commons/
COPY biblioteca-commons/src biblioteca-commons/src
RUN mvn install -f biblioteca-commons/pom.xml -DskipTests -q
COPY cobros-service/pom.xml cobros-service/
COPY cobros-service/src cobros-service/src
RUN mvn clean package -f cobros-service/pom.xml -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/cobros-service/target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
