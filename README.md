# 🐰 OnlyBuns

> A social network platform for rabbit enthusiasts — built with Java Spring Boot, Vue.js, and PostgreSQL.

OnlyBuns lets users share photos of their pets, discover nearby posts on an interactive map, chat in real time, and follow each other's activity. The platform is built with security, scalability, and real-time communication in mind.

---

## ✨ Features

### 👤 Authentication & User Management
- JWT-based authentication with email activation flow
- Rate limiting on login attempts (5 per minute per IP address)
- Bloom Filter used for fast username uniqueness checks during registration
- Concurrent registration conflicts resolved via database transactions
- Automated cleanup of unactivated accounts (runs on the last day of each month)

### 📸 Posts
- Create posts with image upload, description, and map-based location selection
- Location data stored as coordinates and cached for map rendering
- Like and comment on posts
- Concurrent like increments handled safely with optimistic locking and transactions
- Periodic background job compresses images older than one month

### 💬 Comments
- Registered users can comment on posts from accounts they follow
- Rate limit of 60 comments per user per hour enforced at the application level

### 🗺️ Map & Location
- Interactive map centered on the user's profile address
- Discover nearby posts from other users on the map
- Real-time display of rabbit care locations (shelters, vets) received via message queue

### 👥 Social Graph
- Follow and unfollow other users
- Follower/following counts displayed on profiles
- Follow rate limited to 50 per minute to prevent bot abuse
- Concurrent follow actions handled via transactions

### 💬 Real-Time Chat
- WebSocket-based messaging between users
- Group chat support with admin roles (add/remove members)
- New members see the last 10 messages on joining

### 📊 Analytics & Trends
- Network trends page: total posts, top 5 posts of the last 7 days, top 10 all-time posts, top 10 most active likers of the week
- Admin analytics dashboard with weekly/monthly/yearly post and comment counts
- Radial chart showing user activity breakdown (posted, commented only, inactive)

### 📣 Notifications
- Automated email digest sent to users inactive for 7+ days, summarizing recent activity

### 🔗 Integrations
- **Rabbit Care Organization**: receives shelter/vet location data via direct message queue
- **Advertising Agencies**: admins can flag posts for advertising; flagged posts are broadcast via fanout message queue to multiple agency consumers

### 📡 Monitoring
- Prometheus + Grafana integration
- Tracked metrics: average HTTP request duration for post creation (24h), CPU utilization, and active user count (24h)

### ⚙️ Advanced / Optional
- Custom message queue implementation supporting direct and fanout communication patterns
- Custom load balancer with configurable algorithm and retry policy
- Custom rate limiter for comment endpoints (5 requests/minute per user, in-memory)

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot |
| Frontend | Vue.js 3 |
| Database | PostgreSQL |
| Caching | Redis (L2 cache) |
| Real-time | WebSockets (STOMP) |
| Messaging | RabbitMQ + custom queue implementation |
| Maps | Leaflet / OpenLayers |
| Monitoring | Prometheus + Grafana |
| Auth | JWT |
| API Docs | OpenAPI / Swagger |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Redis
- RabbitMQ

### Backend

```bash
cd backend
cp src/main/resources/application.example.properties src/main/resources/application.properties
# Fill in your DB credentials, mail config, and JWT secret
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

### API Documentation

Once the backend is running, visit:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🗄️ Database Seeding

A seed script is available for populating the database with demo data for development and testing:

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.arguments=--seed
```

---

## 🏗️ Architecture Overview

```
┌─────────────┐     REST/WS      ┌──────────────────┐
│   Vue.js    │ ◄──────────────► │  Spring Boot API  │
│  Frontend   │                  │                   │
└─────────────┘                  │  - Auth (JWT)     │
                                 │  - Posts          │
                                 │  - Chat (WS)      │
                                 │  - Map            │
                                 └────────┬─────────┘
                                          │
                    ┌─────────────────────┼────────────────────┐
                    │                     │                    │
             ┌──────▼──────┐     ┌───────▼──────┐    ┌───────▼──────┐
             │  PostgreSQL  │     │    Redis     │    │   RabbitMQ   │
             │  (primary)   │     │   (cache)    │    │  (messaging) │
             └─────────────┘     └──────────────┘    └──────────────┘
```

---

## 🔒 Security

- JWT tokens with configurable expiry
- BCrypt password hashing
- IP-based login rate limiting (5 attempts/min)
- Bloom Filter for O(1) username lookup during registration
- Role-based access control (User / Admin)
- HTTPS enforced in production

---

## 📁 Project Structure

```
onlybuns/
├── backend/
│   ├── src/
│   │   ├── main/java/com/onlybuns/
│   │   │   ├── auth/
│   │   │   ├── user/
│   │   │   ├── post/
│   │   │   ├── comment/
│   │   │   ├── chat/
│   │   │   ├── map/
│   │   │   ├── messaging/
│   │   │   ├── analytics/
│   │   │   └── monitoring/
│   │   └── resources/
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── views/
│   │   ├── components/
│   │   ├── store/
│   │   └── router/
│   └── package.json
└── README.md
```

---

## 📄 License

This project was developed as an academic assignment for the course **Internet Software Architectures** at the Faculty of Technical Sciences, University of Novi Sad (2024/2025).
