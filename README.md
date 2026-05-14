# Salary Management Tool

A full-stack application for managing employee salary data across countries and departments, with salary insights broken down by department, country, and job title.

Built as a take-home assignment for **Incubyte** — designed with TDD, clean code, and small incremental commits.

## Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.3.2, Spring Data JPA |
| Database | H2 (file mode in production, in-memory for tests) |
| Validation | Jakarta Bean Validation + RFC 9457 ProblemDetail |
| Testing | JUnit 5, AssertJ, Mockito, `@DataJpaTest`, `@WebMvcTest` |
| Frontend | React 19, Vite 8, TypeScript 6, Tailwind CSS v4, shadcn/ui |
| Server state | TanStack Query v5 |
| Forms | react-hook-form 7 + Zod v4 |
| Charts | Recharts |
| Backend deploy | Render (Docker) |
| Frontend deploy | Vercel |

## Links

- **Live API:** https://salary-management-16aw.onrender.com
- **Live UI:** https://salary-management-jgf7khep7-aakritis-projects1.vercel.app

## Running Locally

### Prerequisites

- Java 17+
- Node 20+

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

API available at `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

UI available at `http://localhost:5173`.

## Seeding (10,000 employees)

The seeder runs automatically on startup when enabled:

```bash
cd backend
APP_SEED_ENABLED=true ./mvnw spring-boot:run
```

Generates 10,000 employees across 8 countries (IN, US, GB, DE, CA, AU, SG, JP) with realistic salary ranges. Uses JDBC batch inserts in a single transaction — inserting 10,000 rows takes under 2 seconds.

## Running Tests

```bash
cd backend
./mvnw test
```

Tests use an in-memory H2 database — identical dialect to production, no separate setup needed.

## API Endpoints

```
GET    /api/employees            paginated; ?page=0&size=50, optional ?country=, ?department=, ?name=
POST   /api/employees
GET    /api/employees/{id}
PUT    /api/employees/{id}
DELETE /api/employees/{id}       soft delete — archives to employee_history, does not hard delete

GET    /api/insights             headcount, avg/min/max salary, by department, by country
GET    /api/insights/job-titles  ?country=IN — job title breakdown scoped to a country
```

Error responses follow RFC 9457 `ProblemDetail` format.

## Docs

- [Architecture](docs/architecture.md)
- [Decisions](docs/decisions.md)
- [AI Usage Log](docs/ai-usage.md)
