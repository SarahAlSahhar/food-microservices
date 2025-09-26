# Food Ordering Microservices System
**Course:** Advanced Software Engineering  

## Architecture Overview
This project implements a microservices-based food ordering system with 6 core services:

- **Customer Service** (Port 8081) - Customer management and registration
- **Restaurant Service** (Port 8082) - Restaurant and menu management  
- **Order Service** (Port 8083) - Order processing and orchestration
- **Payment Service** (Port 8084) - Payment processing
- **Delivery Service** (Port 8085) - Delivery coordination
- **Rating Service** (Port 8086) - Reviews and ratings

## Docker Services (Implemented)
- **Customer Service** (8081) - ✅ Containerized
- **Restaurant Service** (8082) - ✅ Containerized
- MySQL Database (3307) - ✅ Containerized

## Services Running Natively
- Order Service (8083) - Native Spring Boot
- Payment Service (8084) - Native Spring Boot  
- Delivery Service (8085) - Native Spring Boot
- Rating Service (8086) - Native Spring Boot

## Quick Start with Docker Compose
```bash
# Clone repository
git clone https://github.com/SarahAlSahhar/food-microservices.git
cd food-microservices

# Start all services
docker-compose up --build

# Stop all services
docker-compose down

# Access APIs
Customer Service: http://localhost:8081/customers
Restaurant Service: http://localhost:8082/restaurants

# API Testing Examples
Register Customer:
POST http://localhost:8081/customers/register
{
    "name": "Test User",
    "email": "test@email.com",
    "phone": "+970-599-123456",
    "address": "Gaza, Palestine"
}
Get Restaurants:
GET http://localhost:8082/restaurants

# Technology Stack
Java 17, Spring Boot
MySQL Database
Docker & Docker Compose
REST APIs