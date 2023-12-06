FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY ./target/ms-user-0.0.1-SNAPSHOT.jar ms-user.jar
EXPOSE 8080
EXPOSE 9090
CMD ["java","-jar","ms-user.jar"]