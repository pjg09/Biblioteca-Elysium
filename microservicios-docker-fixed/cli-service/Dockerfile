FROM eclipse-temurin:17-jre

WORKDIR /app

COPY ./cli-service/target/cli-service-1.0.0.jar app.jar

EXPOSE 8090

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
