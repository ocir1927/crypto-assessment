FROM maven:3.8.1-openjdk-17 AS build

WORKDIR /usr/src/app
COPY . /usr/src/app

# Compile and package the application to an executable JAR
RUN mvn clean package -DskipTests=true

FROM eclipse-temurin:17-jre as run

RUN mkdir /app && adduser crypto && chown -R crypto /app
USER crypto

WORKDIR /app

COPY --from=build /usr/src/app/target/crypto-0.0.1-SNAPSHOT.jar crypto.jar
COPY --from=build /usr/src/app/target/classes/application.properties application.properties
COPY --from=build /usr/src/app/prices /app/prices

ENTRYPOINT [ "java", "-jar", "/app/crypto.jar" ]