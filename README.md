# UrbanGent Order Service

A microservice for managing men's fashion orders. Part of the UrbanGent e-commerce platform.

## Features
- Create orders with automatic price calculation
- Inter-service communication with Product Service via REST
- Asynchronous notifications via RabbitMQ
- Swagger API documentation

## Tech Stack
- Java 17, Spring Boot 3.2
- PostgreSQL, RabbitMQ
- JUnit 5, Mockito

## Running
```bash
mvn spring-boot:run
```

API docs: http://localhost:8082/swagger-ui.html
