FROM eclipse-temurin:21-jdk as build
WORKDIR /workspace/app

COPY pom.xml .
COPY src src

RUN apt-get update && apt-get install -y maven
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
VOLUME /tmp
COPY --from=build /workspace/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]