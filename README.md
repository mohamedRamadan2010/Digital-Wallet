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
* **Java 21 (JDK)**
* **Maven 3.9+**
* **Docker & Docker Compose** (Docker Desktop on Windows/Mac, or Docker Engine on Linux)

### Building the Project
Before running the application, you need to build the microservices and package them into executable Spring Boot JARs. A custom local Lombok JAR is included and automatically installed into your local Maven repository during the build process.

**Open your terminal (Command Prompt/PowerShell on Windows, Terminal on Mac/Linux) and run from the root directory:**
```bash
mvn clean package -DskipTests
```
> **Note for Windows Users:** If `mvn` is not recognized, ensure Maven is added to your system's `PATH` environment variable.

### Starting the Infrastructure
The application requires PostgreSQL (database) and Zipkin (distributed tracing). We use a dedicated Docker Compose file for this.

**Run this command to start the backing services:**
```bash
docker compose -f docker-compose.infra.yml up -d
```
*(The `docker compose` command works across Windows, Mac, and Linux as long as Docker Compose is installed).*

### Running the Microservices
With the infrastructure running and the JARs built, you can containerize and orchestrate the entire platform.

**Run this command to build the Docker images and start the services:**
```bash
docker compose -f docker-compose.app.yml up --build -d
```
> **Note:** Wait about 20-30 seconds for the Eureka Server to fully start up and allow all microservices (API Gateway, Identity, Wallet, Transaction, Fraud) to register successfully.

### Accessing the Platform
Once everything is up and running, you can access the following dashboards:
* **Eureka Service Registry:** [http://localhost:8761](http://localhost:8761)
* **Zipkin Tracing:** [http://localhost:9411](http://localhost:9411)
* **API Gateway (Main Entrypoint):** [http://localhost:8080](http://localhost:8080)

### Application Teardown
To cleanly stop and remove all application and infrastructure containers, run:
```bash
docker compose -f docker-compose.app.yml down
docker compose -f docker-compose.infra.yml down
```

### Validation using Postman
Import the Postman Collection located at `docs/wallet-platform.postman_collection.json` into Postman to walk through:
1. Registering Users
2. Logging in to get a JWT token
3. Creating Wallets and Top-ups
4. Conducting P2P Money Transfers via the API Gateway
5. Checking the wallet balance
6. Loading transaction history
7. Logging out

## 📋 Technology Stack
* Java 21 & Spring Boot 3.2.x
* PostgreSQL (Persistence)
* Netflix Eureka (Service Discovery)
* Resilience4j (Circuit Breaker)
* OpenFeign (Inter-service APIs)
* Micrometer & Zipkin (Distributed Tracing)

---
![Java 21](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
