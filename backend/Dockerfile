FROM openjdk:22

WORKDIR /app

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle

RUN microdnf install findutils

COPY src ./src

RUN ./gradlew build --no-daemon

CMD ["./gradlew", "bootRun"]