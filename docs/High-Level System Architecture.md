```mermaid
flowchart TD
    Client([Client / Mobile App])
    
    subgraph Infrastructure
        Gateway[API Gateway<br/>Spring Cloud]
        Registry[Eureka Server<br/>Service Discovery]
    end
    
    subgraph Microservices
        Identity[Identity Service<br/>JWT Auth / Roles]
        Wallet[Wallet Service<br/>Ledger / Balances]
        Transaction[Transaction Service<br/>Saga Orchestrator]
        Fraud[Fraud Service<br/>Rules Engine]
    end
    
    subgraph Databases
        DB_Id[(Identity DB<br/>PostgreSQL)]
        DB_Wal[(Wallet DB<br/>PostgreSQL)]
        DB_Tx[(Transaction DB<br/>PostgreSQL)]
    end
    
    %% Traffic Routing
    Client -->|HTTPS + JWT| Gateway
    Gateway -->|Route: /api/auth| Identity
    Gateway -->|Route: /api/wallets| Wallet
    Gateway -->|Route: /api/transactions| Transaction
    
    %% Service Discovery Registration
    Identity -.->|Registers| Registry
    Wallet -.->|Registers| Registry
    Transaction -.->|Registers| Registry
    Fraud -.->|Registers| Registry
    
    %% Database Connections
    Identity --> DB_Id
    Wallet --> DB_Wal
    Transaction --> DB_Tx
    
    %% Synchronous Inter-service Communication
    Transaction ==>|OpenFeign| Wallet
    Transaction ==>|OpenFeign| Fraud

```