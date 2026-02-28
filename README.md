# 🚀 Digital Wallet & Payment Processing Platform

Welcome to the `wallet-platform`. This repository contains the source code for a production-grade, distributed digital wallet and payment processing system. 

Designed as a showcase of enterprise microservices architecture, this platform facilitates secure user onboarding, wallet management, and real-time peer-to-peer money transfers with integrated, rule-based fraud detection.

## 🏗️ Architecture

This repository is designed around the **Microservices Architecture**.

  1. [System Architecture](docs/High-Level%20System%20Architecture.md)

  2. [Saga Pattern Distributed Transaction Flow](<docs/Saga Pattern (Distributed Transaction Flow).md>)

### Core Components
1. **API Gateway (Spring Cloud Gateway):** Centralized entrypoint. Handles request routing, load balancing, and global JWT authentication.
2. **Eureka Server (Spring Cloud Netflix):** Service Discovery Registry.
3. **Identity Service:** Responsible for User Registration, Authentication, and setting `JWT` tokens.
4. **Wallet Service:** Manages user wallets. Maintains balance and processes credits/debits asynchronously or via orchestration. Uses **Optimistic Locking** to prevent double spending.
5. **Fraud Service:** Analyzes transaction validity. Declines transactions > 10,000 threshold, or if velocity > 3 transactions per minute.
6. **Transaction Service:** Operates the **Saga Pattern (Orchestration)** handling cross-service consistency when sending money between users. Uses **Resilience4j** for circuit breaking and retries.

### Observability
Micrometer + Zipkin are employed to provide distributed tracing across HTTP communication.

## 🚀 Getting Started

### Prerequisites
* Java 21
* Maven 3.9+
* Docker & Docker Compose

### Building the Project
```bash
# From the root directory:
mvn clean install -DskipTests
```

### Starting the Infrastructure
Launch PostgreSQL and Zipkin with Docker Compose:
```bash
docker-compose up -d
or for mac user 
docker compose up -d
```

### Running Microservices
Run the following applications, in order (preferably via IDE):
1. `EurekaServerApplication` (Port 8761)
2. `ApiGatewayApplication` (Port 8080)
3. `IdentityServiceApplication` (Port 8081)
4. `WalletServiceApplication` (Port 8082)
5. `FraudServiceApplication` (Port 8083)
6. `TransactionServiceApplication` (Port 8084)

### Validation using Postman
Import the Postman Collection located at `docs/wallet-platform.postman_collection.json` into Postman to walk through:
1. Registering Users
2. Logging in to get a JWT token
3. Creating Wallets and Top-ups
4. Conducting P2P Money Transfers via the API Gateway
5. Also, you can check the wallet balance
6. load history for transactions
7. log out

## 📋 Technology Stack
* Java 21 & Spring Boot 3.2.x
* PostgreSQL (Persistence)
* Netfilx Eureka (Service Discovery)
* Resilience4j (Circuit Breaker)
* OpenFeign (Inter-service APIs)
* Micrometer & Zipkin (Distributed Tracing)


To run your new platform, follow these steps:

1. Stop any currently running databases or services in IntelliJ or old Docker containers.
2. Ensure you have built your jars by running: mvn clean package -DskipTests
3. Spin up the infrastructure network and backing services: docker compose -f docker-compose.infra.yml up -d
4. Spin up the microservices: docker compose -f docker-compose.app.yml up --build -d
These files are fully ready and the environment variables properly map container names (wallet-postgres, wallet-eureka-server, etc.). Let me know when you try spinning them up!


![Java 21](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
