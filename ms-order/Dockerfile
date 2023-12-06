FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY ./target/ms-order-0.0.1-SNAPSHOT.jar ms-order.jar
EXPOSE 8484
EXPOSE 9494
CMD ["java","-jar","ms-order.jar"]