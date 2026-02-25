# Digital-Wallet
# 🚀 Digital Wallet & Payment Processing Platform

Welcome to the `wallet-platform-monorepo`. This repository contains the source code for a production-grade, distributed digital wallet and payment processing system. 

Designed as a showcase of enterprise microservices architecture, this platform facilitates secure user onboarding, wallet management, and real-time peer-to-peer money transfers with integrated, rule-based fraud detection.

### 🏗️ Architectural Highlights
This system goes beyond basic CRUD operations to tackle the realities of distributed systems:
* **Distributed Transactions:** Implements **Saga Orchestration** to ensure data consistency across isolated microservice databases during money transfers.
* **Concurrency Control:** Utilizes **Optimistic Locking** to absolutely prevent double-spending anomalies during concurrent transaction requests.
* **Fault Tolerance:** Integrates **Resilience4j** (Circuit Breakers & Retries) to gracefully handle downstream service failures and network latency.
* **Modern Tech Stack:** Built on **Java 21** and **Spring Boot 3**, leveraging Spring Cloud Gateway for API routing, Eureka for Service Discovery, and PostgreSQL for robust data persistence.

All services are containerized via Docker and orchestrated via Docker Compose for a seamless local development and evaluation experience.


wallet-platform-monorepo/
├── .github/                   # CI/CD workflows (GitHub Actions)
├── docs/                      # Architecture diagrams, API specs
├── api-gateway/               # Spring Cloud Gateway source code
├── eureka-server/             # Service discovery source code
├── identity-service/          # Identity microservice source code
├── wallet-service/            # Wallet microservice source code
├── transaction-service/       # Transaction microservice source code
├── fraud-service/             # Fraud detection microservice source code
├── docker-compose.yml         # Local infrastructure (Postgres, Zipkin, etc.)
├── init-dbs.sql               # Database initialization script
├── .gitignore                 # Global gitignore (Java, IDEs, OS files)
└── README.md                  # Project overview, setup instructions, architecture


![Java 21](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
