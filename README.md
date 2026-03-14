# Reactive Stock Price Service

## Overview
This project is a **non-blocking reactive RESTful service** built using **Spring WebFlux** and **Project Reactor**. It provides real-time stock price data using reactive APIs and **Server-Sent Events (SSE)**.

The service demonstrates how reactive programming can handle **high concurrency and streaming data efficiently without blocking threads**.

---

## Tech Stack

- Java 17+
- Spring Boot
- Spring WebFlux
- Project Reactor
- Docker
- Docker Compose
- Maven

---

## Project Architecture

The application follows a streamlined reactive architecture:

- **Controller**
  - Handles HTTP requests
  - Manages SSE streams
  - Returns reactive responses (`Mono`, `Flux`)

- **Service**
  - Contains business logic
  - Stores the seeded in-memory stock data
  - Generates random price fluctuations

- **Filter**
  - Intercepts incoming requests
  - Logs request duration
  - Works without blocking the event loop

---

## Project Structure

```
src
 ├── main
 │   ├── java
 │   │   └── com.example.reactive_stock_service
 │   │          ├── ReactiveStockServiceApplication.java
 │   │          ├── StockController.java
 │   │          ├── StockService.java
 │   │          ├── StockPrice.java
 │   │          └── ReactiveRequestLoggingFilter.java
 │   └── resources
 │          └── application.properties
 └── test
     └── java
         └── com.example.reactive_stock_service
                └── StockControllerTest.java
```

Project root files:

```
docker-compose.yml
Dockerfile
.env.example
pom.xml
README.md
```

---

## Seeded Stock Data

The application uses an **in-memory data source**.

| Symbol | Base Price |
|------|------|
| AAPL | 150.00 |
| GOOG | 2800.00 |
| MSFT | 300.00 |
| AMZN | 3400.00 |
| TSLA | 700.00 |

---

# API Endpoints

## 1️⃣ Get Single Stock Price

```
GET /api/stocks/{symbol}
```

### Example Request

```bash
curl http://localhost:8080/api/stocks/AAPL
```

### Success Response (200 OK)

```json
{
 "symbol": "AAPL",
 "price": 150.00,
 "timestamp": 1710000000000
}
```

Requesting an unknown symbol:

```
GET /api/stocks/UNKNOWN
```

Returns:

```
404 NOT FOUND
```

---

## 2️⃣ Get Multiple Stock Prices

```
GET /api/stocks?symbols=AAPL,MSFT
```

### Example Request

```bash
curl "http://localhost:8080/api/stocks?symbols=AAPL,MSFT,UNKNOWN"
```

### Success Response (200 OK)

```json
[
 {
  "symbol": "AAPL",
  "price": 150.00,
  "timestamp": 1710000000000
 },
 {
  "symbol": "MSFT",
  "price": 300.00,
  "timestamp": 1710000000000
 }
]
```

Invalid symbols (like `UNKNOWN`) are **ignored automatically**.

---

## 3️⃣ Stream Stock Prices (SSE)

```
GET /api/stocks/stream/{symbol}
```

This endpoint streams stock price updates **every 2 seconds**.

Prices fluctuate randomly within **±5% of the base price**.

### Example Request

```bash
curl -N http://localhost:8080/api/stocks/stream/MSFT
```

### Example Event Stream

```
id:0
event:price-update
data:{"symbol":"MSFT","price":302.5,"timestamp":1710000002000}

id:1
event:price-update
data:{"symbol":"MSFT","price":298.1,"timestamp":1710000004000}
```

---

# Running the Application (Docker)

## Step 1 — Create Environment File

Copy the example environment file:

```bash
cp .env.example .env
```

---

## Step 2 — Build and Start Containers

```bash
docker-compose up --build -d
```

---

## Step 3 — Verify the Application

```bash
curl http://localhost:8080/api/stocks/AAPL
```

---

# Health Check

The application exposes a **Spring Boot Actuator health endpoint**.

Docker uses this endpoint to verify the container is running correctly.

```bash
curl -f http://localhost:8080/actuator/health
```

Expected response:

```json
{"status": "UP"}
```

---

# Reactive Principles Used

This application follows key **Reactive Programming principles**.

### Non-blocking request handling
A custom `WebFilter` logs request durations using `.doFinally()` without blocking the event loop.

### Reactive Streams
- `Mono` is used for single responses.
- `Flux` is used for multiple elements and streaming data.

### Event Streaming
Server-Sent Events (`ServerSentEvent`) are used for real-time price updates.

### No Blocking Calls
The application contains **no usage of**:

```
.block()
.blockFirst()
.blockLast()
```

This ensures the service remains fully **non-blocking and scalable**.

---

# Author

Reactive Stock Price Service — Spring WebFlux Project