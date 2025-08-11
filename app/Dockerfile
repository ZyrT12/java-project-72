FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY app/gradlew .
COPY app/gradle gradle
COPY app/build.gradle.kts .
COPY app/settings.gradle.kts .

RUN chmod +x ./gradlew

RUN ./gradlew --no-daemon dependencies

COPY app/src src

RUN ./gradlew --no-daemon installDist

FROM eclipse-temurin:21-jre

WORKDIR /opt/app

COPY --from=builder /app/build/install/app /opt/app

EXPOSE 8080

ENV JAVA_OPTS=""

CMD ["/opt/app/bin/app"]
