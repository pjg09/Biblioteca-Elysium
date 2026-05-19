FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn install -N -DskipTests -q
COPY biblioteca-commons/pom.xml biblioteca-commons/
COPY biblioteca-commons/src biblioteca-commons/src
RUN mvn install -f biblioteca-commons/pom.xml -DskipTests -q
COPY circulacion-service/pom.xml circulacion-service/
COPY circulacion-service/src circulacion-service/src
RUN mvn package -f circulacion-service/pom.xml -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/circulacion-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
