# ServiceWatch

## Smart Incident & Service Reliability Platform

ServiceWatch is a backend-focused incident and service reliability platform designed to help engineering teams monitor services, manage incidents, track incident lifecycles, and handle escalations.

The project is built as a modular monolith using Spring Boot and PostgreSQL, with a separate payment-service component prepared for future service-oriented architecture.

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
RESOLVEDIncidents can also be escalated:
OPEN ─────────────→ ESCALATED
ACKNOWLEDGED ─────→ ESCALATED
INVESTIGATING ────→ ESCALATED

An escalated incident can then be resolved:
ESCALATED → RESOLVED

Invalid status transitions are rejected by the application.
Automatic Escalation
Critical incidents can have an escalation deadline.
A scheduled background process checks for incidents whose escalation deadline has passed and automatically changes eligible incidents to:
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
Service Health
Services can have one of three health states:
- HEALTHY
- DEGRADED
- DOWN
Service health considers active incidents and configured health-check endpoints.
Notifications
The platform supports notifications for important incident events such as:
- Incident creation
- Assignment
- Status changes
- Escalation
Notifications can be viewed through the notification API.
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
Testing
- JUnit
- Mockito
- Spring Boot Test
- H2 for test database configuration
DevOps & Containerization
- Docker
- Docker Compose
- Git
- GitHub
Additional Component
- payment-service — separate Spring Boot service prepared for future service-oriented architecture
System Architecture
The current architecture follows a modular monolith approach:
                    Client
                      │
                      ▼
              REST API / Controllers
                      │
                      ▼
                Service Layer
                      │
          ┌───────────┼───────────┐
          ▼           ▼           ▼
       Incident      User       Service
       Management   Management   Health
          │           │           │
          └───────────┼───────────┘
                      ▼
                 JPA / Hibernate
                      │
                      ▼
                  PostgreSQL

Security is applied through Spring Security and JWT authentication.
A separate payment-service application is also included as a foundation for future service-oriented architecture.
User Roles
Role	Permissions
ADMIN	Full incident and user management
TEAM_LEAD	Incident management, assignment and team operations
ENGINEER	View, create and update incidents
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
GET /api/notifications

Additional endpoints are available for incident comments and incident events.
Database
The application uses PostgreSQL with JPA/Hibernate.
Important entities include:
User
Incident
MonitoredService
IncidentEvent
IncidentComment
Notification

Indexes are used for frequently queried incident and notification data, including:
- Incident escalation queries
- Incident service lookup
- Notification lookup by user and creation time
Testing
The backend includes unit and application-context tests.
The test environment uses an in-memory H2 database so that tests do not depend on the local PostgreSQL database.
Current test coverage includes areas such as:
- Incident creation
- Incident status transitions
- Invalid status transitions
- Incident escalation
- Application context loadingRunning Locally
Prerequisites
Make sure the following are installed:
- Java 21
- Maven or Maven Wrapper
- PostgreSQL
- Git
- Docker (optional)
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
4. Run the application
Windows:
mvnw.cmd spring-boot:run

The backend runs on:
http://localhost:8081

Docker
The project includes:
Dockerfile
docker-compose.yml

Docker support is included as part of the project's containerization setup.
Future Enhancements
The following technologies and capabilities are planned for future iterations:
- React frontend integration
- Apache Kafka for event-driven communication
- Redis for caching
- Docker-based deployment improvements
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
- Event-driven architecture concepts
- Testing
- Containerization
- CI/CD
- Cloud deployment
Author
Ayshath Asheeba
Computer Science & Engineering
GitHub:
https://github.com/ayshaasee