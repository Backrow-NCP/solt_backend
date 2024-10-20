FROM openjdk:11-jre-slim

RUN yum install -y jq && \
    yum clean all

ARG JAR_FILE=build/libs/solt-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]
