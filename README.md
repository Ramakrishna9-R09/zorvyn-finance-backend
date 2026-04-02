# Zorvyn Finance Backend

Finance Data Processing and Access Control Backend built for the Zorvyn Backend Developer Internship assignment.

## Author
- Name: Venkata Rama Krishna Kamepalli
- Email: ramakrishna.chowdary2005@gmail.com

## Assignment Fit
This project covers the assignment requirements through a Spring Boot REST API with:
- User and role management
- Role-based access control
- Financial record CRUD and filtering
- Dashboard summary and analytics APIs
- Validation and centralized error handling
- Persistent data handling with H2 for zero-setup local evaluation
- JWT authentication as an optional enhancement

## Tech Stack
- Java 17
- Spring Boot 3.2
- Spring Web
- Spring Security
- Spring Data JPA
- H2 Database
- JWT (`jjwt`)
- Swagger / OpenAPI
- JUnit 5 + MockMvc

## Roles and Permissions

| Role | Dashboard | Read Records | Create Records | Update/Delete Records | Manage Users |
|------|-----------|--------------|----------------|-----------------------|--------------|
| Viewer | Yes | Yes | No | No | No |
| Analyst | Yes | Yes | Yes | No | No |
| Admin | Yes | Yes | Yes | Yes | Yes |

## API Overview

### Authentication
- `POST /api/auth/login`
- `POST /api/auth/register`

### Users
- `GET /api/users`
- `GET /api/users/{id}`
- `POST /api/users`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`

### Financial Records
- `GET /api/financial-records`
- `GET /api/financial-records/{id}`
- `POST /api/financial-records`
- `PUT /api/financial-records/{id}`
- `DELETE /api/financial-records/{id}`

Supported filters on `GET /api/financial-records`:
- `type`
- `category`
- `startDate`
- `endDate`
- `minAmount`
- `maxAmount`

### Dashboard
- `GET /api/dashboard`

Dashboard response includes:
- Total income
- Total expenses
- Net balance
- Transaction count
- Average transaction amount
- Category-wise expense totals
- Monthly trend data
- Recent activity

## Local Run

### Prerequisites
- Java 17+ recommended
- Maven 3.9+ recommended

### Commands
```bash
mvn test
mvn spring-boot:run
```

If Maven is not installed globally, the project can still be built with any local Maven installation by pointing to it explicitly.

### App URLs
- API base: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`
- Health: `http://localhost:8080/actuator/health`

## Seed Data

### Default Admin
- Email: `admin@zorvyn.com`
- Password: `admin123`

### Sample Records
The application seeds roles, one admin user, and sample income/expense records on startup.

## Example Requests

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@zorvyn.com\",\"password\":\"admin123\"}"
```

### Create a Record
```bash
curl -X POST http://localhost:8080/api/financial-records ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer YOUR_TOKEN" ^
  -d "{\"amount\":1500,\"type\":\"INCOME\",\"category\":\"FREELANCE\",\"transactionDate\":\"2026-04-02\",\"description\":\"Project payment\"}"
```

### Filter Records
```bash
curl "http://localhost:8080/api/financial-records?type=EXPENSE&category=RENT" ^
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Validation and Error Handling
- Jakarta Bean Validation is applied at request boundaries
- Consistent API response envelope via `ApiResponse`
- Centralized exception handling using `@RestControllerAdvice`
- Proper use of `400`, `401`, `403`, `404`, and `500`

## Design Decisions
- H2 was chosen for friction-free evaluation and easy local setup
- JWT keeps the API stateless and demonstrates production-style auth flow
- DTOs separate request/response contracts from JPA entities
- Method-level security expresses role rules clearly and close to endpoints
- Soft delete is implemented through a nullable `deletedAt` field for clarity and auditability

## Tests
Integration tests cover:
- Successful admin login
- Viewer access restriction on record creation
- Dashboard access for an authenticated admin

## Verified Build Status
Validated locally with:
- `mvn test`
- `mvn package`

Generated artifact:
- `target/finance-backend-1.0.0.jar`

## Assumptions
- Single-tenant system
- Single-currency record storage
- Email is the unique login identifier
- Users have one role each

## Future Improvements
- Pagination and sorting on list endpoints
- Refresh tokens
- Record ownership rules
- Export APIs
- TestContainers-based integration testing
- PostgreSQL profile for deployment realism
