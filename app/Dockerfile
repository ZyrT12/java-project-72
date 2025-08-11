FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY config config
RUN chmod +x gradlew

RUN ./gradlew --no-daemon dependencies

COPY src src

RUN ./gradlew --no-daemon clean installDist

FROM eclipse-temurin:21-jre
WORKDIR /opt/app

COPY --from=builder /app/build/install/app /opt/app

EXPOSE 7070
ENV JAVA_OPTS=""

CMD ["/opt/app/bin/app"]
