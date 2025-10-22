# ✈️ Airline Booking Platform - Backend

A robust Spring Boot backend for an airline booking system with real-time flight data integration, user management, and booking functionality.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-Hibernate-lightgrey)
![REST API](https://img.shields.io/badge/REST%20API-Fully%20Functional-success)

## 🚀 Features

### Core Backend Features
- **RESTful API** with proper HTTP status codes
- **JWT Authentication & Authorization** (USER/ADMIN roles)
- **Spring Data JPA** with Hibernate ORM
- **MySQL Database** with optimized relationships
- **Real-time Flight Data** integration via AviationStack API
- **Booking Management System** with seat allocation
- **Input Validation** and comprehensive error handling
- **CORS Configuration** for frontend integration

### Advanced Features
- **Lazy vs Eager Loading** optimization
- **Custom Query Methods** with Spring Data JPA
- **Exception Handling** with `@ControllerAdvice`
- **Database Migrations** ready
- **API Documentation** with Swagger/OpenAPI
- **Logging** and monitoring capabilities

## 🛠️ Tech Stack

### Backend Framework
- **Java 17**
- **Spring Boot 3.0**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** (Hibernate Implementation)
- **Spring Validation**

### Database & ORM
- **MySQL 8.0**
- **Hibernate ORM** with JPA
- **Flyway** (for database migrations)

### External Services
- **AviationStack API** - Real-time flight data
- **Maven** - Dependency management

### Development Tools
- **Spring Boot DevTools**
- **Lombok** (Reduces boilerplate code)
- **Spring Boot Test** (JUnit 5, Mockito)

## 📋 Prerequisites

- **Java 17** or higher
- **MySQL 8.0** or higher
- **Maven 3.6** or higher
- **AviationStack API Key** ([Get free key here](https://aviationstack.com/))

## 🗄️ Database Schema

```sql
CREATE DATABASE airline;
USE airline;

-- Users table for authentication
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Flights table for flight information
CREATE TABLE flights (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(20) UNIQUE NOT NULL,
    origin VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    available_seats INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bookings table for reservation system
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    flight_id BIGINT NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    seat_number INT,
    status ENUM('BOOKED', 'CANCELLED') DEFAULT 'BOOKED',
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE,
    UNIQUE KEY unique_booking (flight_id, seat_number)
);

-- Aircraft information table
CREATE TABLE aircrafts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    model VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    manufacturer VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

MIT License gives everyone the freedom to use, modify, and distribute your code with minimal restrictions.