FROM maven:3.8.6-eclipse-temurin-11-alpine

WORKDIR /Backend

COPY . .

RUN mvn clean install

EXPOSE 8081

CMD mvn spring-boot:run