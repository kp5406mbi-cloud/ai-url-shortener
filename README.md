# AI URL Shortener

A production-ready URL Shortener built using Spring Boot that allows users to create, manage, and analyze shortened URLs efficiently. The application leverages Redis caching for fast URL resolution, PostgreSQL for persistent storage, JWT-based authentication for security, and Docker for containerized deployment.

## Features

### Authentication & Authorization

* User Registration and Login
* JWT-based Authentication
* Secure API Access
* Role-Based Access Control

### URL Shortening

* Generate unique short URLs
* Custom URL aliases
* URL expiration support
* Redirect users to original URLs

### Analytics Dashboard

* Total Click Tracking
* URL Usage Statistics
* Most Visited URLs
* User-specific URL Analytics

### Performance Optimization

* Redis Caching for frequently accessed URLs
* Reduced database load
* Faster URL redirection

### Deployment & DevOps

* Dockerized Application
* PostgreSQL Database Integration
* Environment Variable Configuration
* Production-ready Architecture

---

## Tech Stack

### Backend

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* JWT Authentication

### Database

* PostgreSQL

### Cache

* Redis

### DevOps

* Docker
* Docker Compose

### Build Tool

* Maven

---

## System Architecture

```text
Client
   |
   v
Spring Boot REST APIs
   |
   +------ JWT Authentication
   |
   +------ PostgreSQL Database
   |
   +------ Redis Cache
   |
   v
Short URL Generation & Analytics
```

---

## API Endpoints

### Authentication

| Method | Endpoint           | Description   |
| ------ | ------------------ | ------------- |
| POST   | /api/auth/register | Register User |
| POST   | /api/auth/login    | User Login    |

### URL Management

| Method | Endpoint         | Description              |
| ------ | ---------------- | ------------------------ |
| POST   | /api/url/shorten | Create Short URL         |
| GET    | /{shortCode}     | Redirect to Original URL |
| GET    | /api/url/my-urls | Get User URLs            |
| DELETE | /api/url/{id}    | Delete URL               |

### Analytics

| Method | Endpoint                 | Description               |
| ------ | ------------------------ | ------------------------- |
| GET    | /api/analytics/{id}      | URL Analytics             |
| GET    | /api/analytics/dashboard | User Dashboard Statistics |

---

## Installation

### Clone Repository

```bash
git clone https://github.com/yourusername/ai-url-shortener.git

cd ai-url-shortener
```

### Configure Environment Variables

```properties
DB_URL=jdbc:postgresql://localhost:5432/urlshortener
DB_USERNAME=postgres
DB_PASSWORD=password

JWT_SECRET=your_secret_key

REDIS_HOST=localhost
REDIS_PORT=6379
```

### Run PostgreSQL & Redis

```bash
docker-compose up -d
```

### Build Application

```bash
mvn clean install
```

### Run Application

```bash
mvn spring-boot:run
```

Application will start at:

```text
http://localhost:8080
```

---

## Docker Deployment

Build Docker Image

```bash
docker build -t ai-url-shortener .
```

Run Container

```bash
docker run -p 8080:8080 ai-url-shortener
```

---

## Future Enhancements

* AI-powered malicious URL detection
* QR Code generation
* Custom branded URLs
* Geo-location analytics
* Rate limiting
* OAuth2 Login (Google/GitHub)
* URL preview support

---

## Learning Outcomes

This project demonstrates:

* REST API Design
* Authentication & Authorization using JWT
* Spring Security Implementation
* Database Modeling with PostgreSQL
* Redis Caching Strategies
* Docker Containerization
* Backend Performance Optimization
* Production Deployment Practices

---

## Author

**Kumar Piyush**

Integrated MSc Mathematics & Computing
BIT Mesra, Ranchi

GitHub: https://github.com/kp5406mbi-cloud
