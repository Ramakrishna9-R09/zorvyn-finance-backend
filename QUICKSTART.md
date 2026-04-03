# Quick Start

## Run
```bash
cd zorvyn-finance-backend
mvn test
mvn spring-boot:run
```

## Default Admin
- Email: `admin@zorvyn.com`
- Password: `admin123`

## Useful URLs
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- H2 Console: `http://localhost:8080/h2-console`
- Health: `http://localhost:8080/actuator/health`

## PostgreSQL Mode
Set `SPRING_PROFILES_ACTIVE=postgres` and provide `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` before starting the app if you want to run it against PostgreSQL.

## Main Endpoints
- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/dashboard`
- `GET /api/financial-records`
- `POST /api/financial-records`
- `GET /api/users`

See `README.md` for the full API summary and design notes.
