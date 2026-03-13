# Reactive Stock Price Service

This is a non-blocking, reactive RESTful service built with Spring WebFlux and Project Reactor. It provides real-time, simulated stock price data using standard request-response endpoints and Server-Sent Events (SSE) for live streaming.

## Prerequisites
To run this application, you only need:
* **Docker** installed
* **Docker Compose** installed

*(Note: You do not need Java or Maven installed on your host machine, as the application is fully containerized!)*

## Setup and Run Instructions

1. **Environment Variables**
   The project requires a `.env` file for configuration. Copy the provided example file to create it:
   ```bash
   cp .env.example .env