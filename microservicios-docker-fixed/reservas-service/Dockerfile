# Dockerfile optimizado - usa JAR pre-compilado
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar solo el JAR compilado
COPY ./reservas-service/target/reservas-service-1.0.0.jar app.jar

EXPOSE 8088
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
