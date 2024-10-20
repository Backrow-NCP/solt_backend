FROM openjdk:11-jre-alpine

RUN apk add --no-cache jq

ARG JAR_FILE=build/libs/solt-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]
