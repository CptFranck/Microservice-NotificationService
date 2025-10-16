# 📦 Microservice - Notification Service

The **Notification Service** is a real-time component of the microservice architecture.  
It manages **user notifications**, subscribes to **Kafka topics** emitted by other services (e.g., Booking, Inventory), and updates the front-end via **WebSocket/STOMP**.

---

## 🚀 Overview

This service is responsible for providing **instant updates to connected clients**.  
It listens to events published on Kafka topics and broadcasts them to users in real-time via WebSocket.  
The front-end can subscribe to `/user/{userId}/bookings` or other destinations to receive notifications.

---

## ⚙️ Key Features

- **Spring Boot 3.5.6** – Core microservice framework  
- **Spring WebSocket / STOMP** – Real-time communication with clients  
- **Spring Security Messaging** – Securing WebSocket endpoints  
- **Apache Kafka** – Event-driven communication from other microservices  
- **Lombok** – Boilerplate reduction  
- **Common DTOs** – Shared event/data structures across services  
- **Java 21** – Runtime language  
- **Spring Boot Test / Kafka Test** – Unit and integration testing  

---

## 🧩 Architecture Integration

![Architecture du projet](docs/schemaProject.png)

The Notification Service is part of a **5-repository microservice ecosystem**:

1. **Common** – Shared DTOs and utilities used across services.  
2. **Booking Service** – Handles bookings, emits events to update inventory and notify users.  
3. **Inventory Service** – Maintains venue and event stock, emits events for updates.  
4. **Notification Service** – Subscribes to Kafka topics from Booking and Inventory, pushes updates to front-end via WebSocket.  
5. **API Gateway** – Central entry point for external clients, handles routing, security, WebSocket proxying, and documentation.

The Notification Service connects primarily with **Kafka topics** from other services and pushes real-time notifications to users.  
Authentication and authorization for WebSocket endpoints are enforced using **Keycloak**.
