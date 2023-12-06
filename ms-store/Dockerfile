FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY ./target/ms-store-0.0.1-SNAPSHOT.jar ms-store.jar
EXPOSE 8282
EXPOSE 9292
CMD ["java","-jar","ms-store.jar"]