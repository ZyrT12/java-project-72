FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY app/gradlew .
COPY app/gradle gradle
COPY app/build.gradle.kts .
COPY app/settings.gradle.kts .
COPY app/src src
COPY app/config config

RUN ./gradlew --no-daemon dependencies

COPY src src
COPY config config

RUN ./gradlew --no-daemon clean installDist

FROM eclipse-temurin:21-jre
WORKDIR /opt/app

COPY --from=builder /app/build/install/app /opt/app

EXPOSE 7070
ENV JAVA_OPTS=""

CMD ["/opt/app/bin/app"]
