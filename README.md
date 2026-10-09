# ServiceWatch

## Smart Incident & Service Reliability Platform

ServiceWatch is a backend-focused incident and service reliability platform designed to help engineering teams monitor services, manage incidents, track incident lifecycles, and handle escalations.

The core application follows a modular monolith architecture built with Spring Boot and PostgreSQL. Redis is used for caching, while Apache Kafka provides event-driven communication for incident events. A separate payment-service component is included as an independent Spring Boot service.

---

## Overview

ServiceWatch provides a centralized platform for engineering teams to:

- Create and manage incidents
- Assign incidents to engineers
- Track incident status and severity
- Automatically escalate critical incidents
- Maintain an incident timeline
- Add incident comments
- Monitor service health
- Manage users and roles
- Send and manage notifications
- Cache frequently accessed service data using Redis
- Publish and consume incident events using Apache Kafka
- Secure APIs using JWT authentication
- Control access using role-based authorization

---

## Features

### Authentication & Authorization

- JWT-based authentication
- BCrypt password encryption
- Role-based access control
- Protected REST APIs
- Method-level authorization using Spring Security

### User Management

Supported roles:

- `ADMIN`
- `TEAM_LEAD`
- `ENGINEER`
- `VIEWER`

Users can be created, updated, retrieved, and deleted according to their permissions.

### Incident Management

Incidents support:

- Title
- Description
- Severity
- Status
- Service association
- Engineer assignment
- Creation timestamp
- Resolution timestamp
- Escalation deadline

Supported severity levels:

- `LOW`
- `MEDIUM`
- `HIGH`
- `CRITICAL`

### Incident Lifecycle

The incident lifecycle follows controlled status transitions:

```text
OPEN
  ↓
ACKNOWLEDGED
  ↓
INVESTIGATING
  ↓
RESOLVED

Incidents can also be escalated:
OPEN ─────────────→ ESCALATED
ACKNOWLEDGED ─────→ ESCALATED
INVESTIGATING ────→ ESCALATED

ESCALATED ────────→ RESOLVED

Invalid status transitions are rejected by the application.
Automatic Escalation
Critical incidents can have an escalation deadline.
A scheduled background process checks for critical incidents whose escalation deadline has passed and automatically changes eligible incidents to:
ESCALATED

The assigned engineer can also receive an escalation notification.
Incident Timeline
ServiceWatch records important incident events such as:
- Incident creation
- Assignment
- Status changes
- Escalation
- Resolution
- Incident updates
This provides an audit-style timeline for incidents.
Service Health Monitoring
Monitored services can have one of three health states:
- HEALTHY
- DEGRADED
- DOWN
Service health is determined using configured health-check endpoints and active incident conditions.
Automated background health checks periodically evaluate monitored services and record health information such as:
- Health status
- Response time
- Last health check time
Notifications
The platform supports notifications for important incident events such as:
- Incident creation
- Assignment
- Status changes
- Escalation
Notifications can be retrieved and marked as read through the notification API.
Redis Caching
Redis is used to cache frequently accessed monitored service data.
The service cache is invalidated when monitored services are created, updated, deleted, or when service health information changes.
Kafka Event Streaming
Apache Kafka is used for event-driven incident processing.
When an incident is created, ServiceWatch publishes an event to the:
incident-events

Kafka topic.
A Kafka consumer listens to the topic and processes incoming incident events.
The current flow is:
IncidentService
      ↓
KafkaProducerService
      ↓
Kafka Topic
incident-events
      ↓
KafkaConsumerService

The Kafka integration has been tested using real incident creation and message consumption.
Validation & Exception Handling
The backend uses centralized exception handling for common API errors.
Examples include:
- Invalid incident status → 400 Bad Request
- Missing service → 400 Bad Request
- Incident not found → 404 Not Found
- Service not found → 404 Not Found
- User not found → 404 Not Found
- Notification access denied → 403 Forbidden
- Request validation errors → 400 Bad Request
Tech Stack
Backend
- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Hibernate
- Spring Security
- JWT
- BCrypt
- Maven
Database
- PostgreSQL
- H2 for test database configuration
Caching
- Redis
- Spring Data Redis
Messaging
- Apache Kafka
- Spring Kafka
Testing
- JUnit
- Mockito
- Spring Boot Test
- H2
DevOps & Containerization
- Docker
- Docker Compose
- Git
- GitHub
Additional Component
- payment-service — separate Spring Boot service included as a foundation for future service-oriented architecture
System Architecture
The core application follows a modular monolith architecture.
                         Client
                           │
                           ▼
                  REST API / Controllers
                           │
                           ▼
                      Service Layer
                           │
          ┌────────────────┼────────────────┐
          ▼                ▼                ▼
      Incidents          Users           Services
      Management       Management      Health Checks
          │                                 │
          │                                 ▼
          │                            Redis Cache
          │
          ▼
     PostgreSQL
          │
          ▼
   Incident Events
          │
          ▼
   Kafka Producer
          │
          ▼
  incident-events Topic
          │
          ▼
   Kafka Consumer

Security is applied through Spring Security and JWT authentication.
Redis provides caching for monitored service data, while Kafka provides event-driven communication for incident events.
A separate payment-service Spring Boot application is also included as an independent service component.
User Roles
Role	Permissions
ADMIN	Full incident, service, and user management
TEAM_LEAD	Incident management, assignment, and team operations
ENGINEER	View, create, and update incidents
VIEWER	Read-only access


Authorization is enforced at the API level using Spring Security method-level security.
API Overview
Authentication
POST /api/auth/login

Users
POST   /api/users
GET    /api/users
GET    /api/users/{id}
PUT    /api/users/{id}
DELETE /api/users/{id}

Incidents
POST   /api/incidents
GET    /api/incidents
GET    /api/incidents/{id}
PUT    /api/incidents/{id}
PATCH  /api/incidents/{id}/status
PUT    /api/incidents/{incidentId}/assign/{userId}
DELETE /api/incidents/{id}

Services
POST   /api/services
GET    /api/services
GET    /api/services/{id}
PUT    /api/services/{id}
DELETE /api/services/{id}

Notifications
GET   /api/notifications
PATCH /api/notifications/{id}/read
PATCH /api/notifications/read-all

Additional endpoints are available for incident comments and incident events.
Database
The application uses PostgreSQL with JPA/Hibernate.
Important entities include:
- User
- Incident
- MonitoredService
- IncidentEvent
- IncidentComment
- Notification
Indexes are used for frequently queried incident and notification data, including:
- Incident escalation queries
- Incident service lookup
- Notification lookup by user and creation time
Testing
The backend includes unit and application-context tests.
The test environment uses an in-memory H2 database so that tests do not depend on the local PostgreSQL database.
Current tests cover areas such as:
- Incident creation
- Incident status transitions
- Invalid status transitions
- Incident escalation
- Application context loading
The current test suite passes successfully:
Tests run: 6
Failures: 0
Errors: 0
BUILD SUCCESS

Running Locally
Prerequisites
Make sure the following are installed:
- Java 21
- Maven or Maven Wrapper
- PostgreSQL
- Git
- Docker
1. Clone the repository
git clone https://github.com/ayshaasee/serviceWatch.git
cd serviceWatch

2. Configure environment variables
The application expects the following environment variables:
DB_PASSWORD
JWT_SECRET

These values should be configured in your local environment.
Do not commit real credentials or secrets to GitHub.
3. Configure PostgreSQL
Create the database:
serviceWatch

Then make sure the PostgreSQL username and environment configuration match your local setup.
4. Run with Maven
Windows:
mvnw.cmd spring-boot:run

The backend runs on:
http://localhost:8081

Docker
The project includes:
Dockerfile
docker-compose.yml

Docker Compose is used to run the backend and supporting infrastructure, including:
- PostgreSQL
- Redis
- Kafka
- payment-service
Future Enhancements
The following capabilities are planned for future iterations:
- React frontend integration
- GitHub Actions CI/CD
- AWS deployment
- Centralized logging
- Metrics and monitoring
- Advanced service health checks
- Rate limiting
- Load testing
- More comprehensive automated testing
- Further decomposition into event-driven services
Project Goals
ServiceWatch is being developed as a practical software engineering project to demonstrate experience with:
- Backend development
- REST API design
- Authentication and authorization
- Database design
- Incident management
- Event-driven architecture
- Caching
- Testing
- Containerization
- Distributed system concepts
Author
Ayshath Asheeba
Computer Science & Engineering
GitHub:
https://github.com/ayshaasee
