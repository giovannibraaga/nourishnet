# NourishNet

NourishNet is a Java and Spring Boot backend application for managing and coordinating food donations and deliveries. The project was designed to support the end-to-end donation lifecycle, from user and donation registration to delivery tracking, matching, alerts and event auditing.

## Features

- User registration and authentication
- Donation management
- Donation matching with recipient organizations
- Delivery tracking and proof of delivery
- Alerts and notifications
- Donation feed
- Administrative operations
- Event logs and audit information

## Technologies

- Java 17
- Spring Boot
- Maven
- Firebase Authentication and JWT validation
- Oracle Autonomous Database
- REST APIs
- Database migrations
- Logback
- Automated tests

## Architecture and project structure

The backend is organized into application, domain, infrastructure and support concerns:

```text
src/
├── main/
│   ├── java/
│   │   └── backend/
│   │       └── nourishnet/
│   │           ├── adapter/       # Adapters and integrations
│   │           ├── api/           # REST controllers and API contracts
│   │           ├── config/        # Application configuration
│   │           ├── domain/        # Domain models and business rules
│   │           ├── dto/           # Request and response objects
│   │           ├── jobs/           # Scheduled and background jobs
│   │           ├── repository/     # Data access layer
│   │           ├── security/       # Authentication and authorization
│   │           ├── service/        # Application services
│   │           ├── shared/         # Shared components
│   │           └── support/        # Supporting infrastructure
│   └── resources/
│       ├── application.properties
│       ├── logback-spring.xml
│       ├── db/migration/
│       ├── static/
│       └── templates/
└── test/
    └── java/
        └── backend/
            └── nourishnet/
                └── NourishnetApplicationTests.java
```

## REST API

The API base path is:

```text
/api
```

### Authentication and users

- `POST /api/auth/signup` - Register a user
- `POST /api/auth/login` - Authenticate a user
- `POST /api/users` - Create a user as an administrator
- `GET /api/users/{id}` - Retrieve a user by ID
- `PUT /api/users/{id}` - Update a user by ID

### Donations

- `POST /api/donations` - Create a donation
- `GET /api/donations` - List donations
- `GET /api/donations/{id}` - Retrieve donation details
- `PUT /api/donations/{id}` - Update a donation

### Deliveries

- `POST /api/deliveries` - Register a delivery
- `GET /api/deliveries` - List deliveries
- `GET /api/deliveries/{id}` - Retrieve delivery details
- `PUT /api/deliveries/{id}/proof` - Submit proof of delivery

### Donation matching

- `POST /api/matches/{id}/accept` - Accept a donation match
- `GET /api/matches` - List donation matches

### Feed

- `GET /api/feed` - List available donations in the feed

### Alerts

- `GET /api/alerts` - List alerts
- `GET /api/alerts/count` - Count alerts

### Administration

- `GET /api/admin/msg` - Retrieve an administrative message

### Event logs

- `GET /api/event-logs` - List event logs

## Authentication and security

Firebase Authentication is used for user identity and JWT-based access validation.

Protected endpoints require a Firebase ID token in the request header:

```http
Authorization: Bearer <firebase-id-token>
```

Administrative operations require the appropriate application role.

Firebase credentials and database credentials must be supplied through a secure environment-specific configuration. Do not commit service-account JSON files, private keys, passwords or production connection details to the repository.

## Running the project

### Requirements

- JDK 17 or later
- Maven 3.8 or later, or the included Maven Wrapper
- Firebase project and credentials
- Oracle Autonomous Database or another configured Oracle database

### Configure the application

Update the environment-specific values used by `application.properties`, including database, Firebase and application settings.

Keep local, staging and production configuration separate and store secrets outside source control.

### Start the application

From the project root:

```bash
./mvnw spring-boot:run
```

The application starts on the default Spring Boot port unless another port is configured.

## Tests

Run the automated tests with:

```bash
./mvnw test
```

## Project status

NourishNet is a portfolio and academic project demonstrating Java backend development, REST API design, authentication, relational data management, donation workflows and delivery tracking.
