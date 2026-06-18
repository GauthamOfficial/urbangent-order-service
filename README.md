# UrbanGent Order Service

A microservice for managing men's fashion orders. Part of the UrbanGent e-commerce platform.

## Features
- Create orders with automatic price calculation
- Inter-service communication with Product Service via REST
- Asynchronous event publishing via RabbitMQ
- PostgreSQL database with JPA/Hibernate
- Swagger/OpenAPI documentation
- CI/CD pipeline with GitHub Actions
- SonarCloud code quality analysis
- JaCoCo code coverage reporting

## Tech Stack
- Java 17, Spring Boot 3.2
- Spring Data JPA, PostgreSQL
- Spring AMQP (RabbitMQ)
- RestTemplate for inter-service communication
- SpringDoc OpenAPI (Swagger)
- JUnit 5, Mockito
- GitHub Actions CI/CD
- SonarCloud, JaCoCo

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create a new order |

## How It Works
1. Client sends an order request with `customerId`, `productId`, and `quantity`
2. Order Service calls Product Service to fetch product details
3. Calculates `totalPrice = unitPrice × quantity`
4. Saves the order with status `PENDING`
5. Publishes an `OrderEvent` to RabbitMQ for the Notification Service

## Running Locally

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL, RabbitMQ (or use Docker Compose)
- Product Service running on port 8081

### Start Infrastructure
```bash
docker-compose up -d
```

### Run the Service
```bash
export DB_HOST=localhost DB_PORT=5433 DB_NAME=order_db DB_USER=admin DB_PASSWORD=admin123
export RABBITMQ_HOST=localhost PRODUCT_SERVICE_URL=http://localhost:8081
mvn spring-boot:run
```

### Run Tests
```bash
mvn test
```

## API Documentation
Swagger UI: http://localhost:8082/swagger-ui.html

## CI/CD
- **GitHub Actions**: Runs tests and builds on every pull request to main
- **SonarCloud**: Automated code quality and security analysis
- **SonarCloud Dashboard**: https://sonarcloud.io/project/overview?id=GauthamOfficial_urbangent-order-service
