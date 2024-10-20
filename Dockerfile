FROM openjdk:11-jre-slim

RUN apt-get update && \
    apt-get install -y jq && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ARG JAR_FILE=build/libs/solt-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]
