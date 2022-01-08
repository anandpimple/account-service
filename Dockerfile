FROM openjdk:11-jre-slim
WORKDIR /app
COPY /build/libs/account-service.jar .
EXPOSE 8080/tcp
CMD java -jar account-service.jar
