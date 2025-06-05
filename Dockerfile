FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the generated JAR file
COPY build/libs/p2p.transport-0.0.1-SNAPSHOT.jar app.jar

# Copy application properties
COPY src/main/resources/application.yml /app/application.yml

# Copy Firebase service account JSON file
COPY src/main/resources/p2ptransport-95fe4-firebase-adminsdk-fbsvc-f7942f2d63.json /app/p2ptransport-95fe4-firebase-adminsdk-fbsvc-f7942f2d63.json

EXPOSE 8080

CMD ["java", "-jar", "app.jar", "--spring.config.location=file:/app/application.yml"]