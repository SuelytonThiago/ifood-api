# IFOOD-API

# About this project
This project was developed based on three microservices that communicate with each other through gRPC and RabbitMQ. RabbitMQ is only used when creating a new establishment. Before the actual creation, the endpoint first creates the user who will be responsible for the store, returning the generated user ID to then create the new establishment. If an error occurs during the creation of the new store, a message is sent to the message queue containing the user ID that must be removed.

To handle authentication and authorization of access to certain endpoints, Spring Security with Json Web Token (JWT) was implemented. This allows you to ensure that only authorized users can access certain functionality.

ScheduledExecutorService was used to schedule a task where every 10 minutes all items that have been in the user's bag for more than 10 hours will be deleted.

To ensure the proper functioning of the entire API, unit tests and integrated tests were developed. Integrated tests use TestContainer to provision an exclusive PostgreSQL database for testing, ensuring an isolated and replicable environment for evaluating functionalities.

This set of practices, from communication between microservices to security and comprehensive testing, contributes to the robustness and reliability of the project as a whole.

## ms-order
![ms-order-diagram](https://github.com/SuelytonThiago/ifood-api/assets/117487495/ef6bd870-f936-4c8c-ae43-7008541601b9)

## ms-store
![ms-store-diagram](https://github.com/SuelytonThiago/ifood-api/assets/117487495/20cb1370-8ce3-4dbe-b44d-a31957c8a6a9)

## ms-user
![ms-user-diagram](https://github.com/SuelytonThiago/ifood-api/assets/117487495/c5eb14b9-4a81-4c55-9884-bbbf3e777688)

![ifood-api](https://github.com/SuelytonThiago/ifood-api/assets/117487495/39d7746e-a870-41b7-9e55-144de4ed70eb)



# Technologies used

- Java
- Spring boot
- Maven
- Jpa/Hibernate
- Spring Security
- Json Web Token
- PostgreSQL Database
- ScheduledExecutorService
- Swagger
- gRPC
- RabbitMQ

# How to run the project

## clone repository
**git clone `https://github.com/SuelytonThiago/ifood-api`**

## Start application containers

To start the containers, open your terminal in administrator mode, then go to the folder where the project was cloned and write the following command:
**docker-compose up -d --build**.  

## Test this application

To test the application, you can use postman or access the swagger documentation through the links below:  

ensure that all containers are working properly before accessing the links below!  

- **ms-user:** http://localhost:8080/swagger-ui.html   
- **ms-store:** http://localhost:8282/swagger-ui.html  
- **ms-order:** http://localhost:8484/swagger-ui.html  
  
To access some endpoints you need to be authenticated. Use the following credentials to authenticate to the application:  

### user:
- **email:** `ana@example.com` 
- **password:** senha123
  
### admin:
- **email:** `adm@example.com`
- **password:** senha123
    
# Author 
- Suelyton Thiago de Lima souza

If you encounter any errors, please contact support using the link below:  
https://www.linkedin.com/in/suelyton-souza-0baaa127a/
