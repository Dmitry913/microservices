FROM maven:3.6.0-jdk-11-slim as builder
WORKDIR build_project
COPY . .
RUN ["mvn", "clean", "package"]
RUN ["mv","./target/doker-web-server-1.0-SNAPSHOT-jar-with-dependencies.jar", "/build_project/artifact.jar"]

FROM openjdk:11-jre-slim
WORKDIR /project
COPY --from=builder /build_project/artifact.jar .
CMD ["java", "-jar", "/project/artifact.jar"]
