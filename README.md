# Real-Time Campus Safety Alert System

A distributed, event-driven microservices platform for real-time campus safety alerting. The system ingests safety incidents, processes and enriches them, fans out notifications across multiple delivery channels in under 2 seconds, and provides students with a live situational awareness dashboard.

## Architecture

The system follows an event-driven microservices pattern with four Spring Boot services communicating asynchronously through Apache Kafka.

```
┌─────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│  Alert Ingestion │────▶│ Alert Processing  │────▶│  Notification    │
│  Service         │     │ Service           │     │  Service         │
│  (Port 8081)     │     │ (Port 8082)       │     │  (Port 8083)     │
└─────────────────┘     └──────────────────┘     └──────────────────┘
        │                        │                        │
        ▼                        ▼                        ▼
   alert-created           alert-enriched        alert-delivery-status
   (Kafka Topic)           (Kafka Topic)           (Kafka Topic)
                                                         │
┌────────────────────────────────────────────────────────┘
▼
┌──────────────────┐     ┌──────────────────┐
│  Dashboard        │────▶│  React Frontend   │
│  Service          │     │  (Port 3000)      │
│  (Port 8080)      │     └──────────────────┘
└──────────────────┘
```

### Services

| Service | Port | Responsibility |
|---------|------|---------------|
| Alert Ingestion | 8081 | Accepts alert creation, validates, persists to PostgreSQL, publishes to Kafka |
| Alert Processing | 8082 | Enriches alerts with geo-radius affected buildings, determines recipient lists |
| Notification | 8083 | Fans out delivery across email, SMS, push channels; tracks delivery status |
| Dashboard | 8080 | GraphQL API gateway, WebSocket connections, real-time updates |

## Tech Stack

| Layer | Technologies |
|-------|-------------|
| Backend | Java 17, Spring Boot 3.2, Spring Kafka, Spring WebSocket, Spring Data JPA |
| API | Spring GraphQL, Apollo Client |
| Messaging | Apache Kafka (3-node cluster) |
| Frontend | React 18, TypeScript, Apollo Client, Leaflet.js, Tailwind CSS |
| Database | PostgreSQL 16, Redis 7 |
| Observability | Prometheus, Grafana, Micrometer, Spring Actuator |
| Infrastructure | Docker Compose, GitHub Actions CI/CD |

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose
- Node.js 18+

### Run Infrastructure

```bash
docker compose up -d
```

This starts Kafka (3 brokers), Zookeeper, PostgreSQL, Redis, Prometheus, and Grafana.

### Build & Run Services

```bash
# Build all services
mvn clean package -DskipTests

# Run individual services
cd alert-ingestion-service && mvn spring-boot:run
cd alert-processing-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd dashboard-service && mvn spring-boot:run
```

### Create a Test Alert

```bash
curl -X POST http://localhost:8081/api/alerts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Suspicious activity near SAC",
    "description": "Unidentified individual reported near Student Activities Center",
    "severity": "HIGH",
    "lat": 40.9136,
    "lng": -73.1235,
    "campusZone": "Student Activities Center"
  }'
```

## Monitoring

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3001 (admin/admin)

## Project Structure

```
campus-safety-alert-system/
├── docker-compose.yml
├── pom.xml                          # Parent POM (multi-module)
├── alert-ingestion-service/         # Alert creation & Kafka producer
├── alert-processing-service/        # Event enrichment & processing
├── notification-service/            # Multi-channel notification delivery
├── dashboard-service/               # GraphQL API & WebSocket gateway
├── frontend/                        # React dashboard
├── prometheus/                      # Prometheus configuration
└── grafana/                         # Grafana dashboard configs
```

## Author

**Priya More** - Stony Brook University
