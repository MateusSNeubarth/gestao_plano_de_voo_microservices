FROM eclipse-temurin:25.0.4.1_1-jdk-noble AS build
WORKDIR /workspace/app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline
COPY src src
RUN ./mvnw -B -ntp clean verify

FROM eclipse-temurin:25.0.4.1_1-jre-noble AS runtime
WORKDIR /app
RUN groupadd --gid 10001 app && useradd --uid 10001 --gid app --no-create-home app
COPY --from=build --chown=10001:10001 /workspace/app/target/app.jar ./app.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
