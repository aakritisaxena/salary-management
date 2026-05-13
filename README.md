# Salary Management Tool

A full-stack application for managing employee salary data across countries and departments, with built-in salary insights (statistics, percentile distributions, department comparisons).

Built as a take-home assignment for **Incubyte** — designed with TDD, clean code, and small incremental commits.

## Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.x, Spring Data JPA |
| Database | SQLite (`sqlite-jdbc` + `hibernate-community-dialects`) |
| Validation | Jakarta Bean Validation |
| Testing | JUnit 5, AssertJ, Mockito, `@DataJpaTest` |
| Frontend | React 18, Vite, TypeScript, Tailwind CSS, shadcn/ui |
| Server state | TanStack Query |
| Forms | react-hook-form + zod |
| Charts | Recharts |
| Backend deploy | Render |
| Frontend deploy | Vercel |

## Running Locally

### Prerequisites

- Java 17+
- Maven 3.9+
- Node 20+ (frontend only)

### Backend

```bash
cd backend
mvn spring-boot:run
```

API available at `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

UI available at `http://localhost:5173` *(added in Phase 4)*.

## Seeding (10,000 employees)

```bash
cd backend
SPRING_APPLICATION_JSON='{"app":{"seed":{"enabled":true}}}' mvn spring-boot:run
```

The seeder generates 10,000 employees across 12 countries with realistic salary ranges per country.

**Performance benchmarks** *(updated after Phase 3)*:

| Strategy | Duration |
|----------|----------|
| Naive JPA `saveAll` | ~XX s |
| JDBC `batchUpdate` (batch 1000) | ~XX s |
| Speedup | ~XXx |

## Running Tests

```bash
cd backend
mvn test
```

Tests use an in-memory SQLite database (same dialect as production, no H2 substitution).

## API Endpoints

```
GET    /api/employees                              (paginated, ?page=0&size=50, optional ?country=, ?department=)
POST   /api/employees
GET    /api/employees/{id}
PUT    /api/employees/{id}
DELETE /api/employees/{id}                         (archives to employee_history, does not hard delete)

GET    /api/insights/salary-by-country?country=IN
GET    /api/insights/salary-by-job-title?country=IN&jobTitle=Engineer
GET    /api/insights/distribution?country=IN       (p25, p50, p75, p90)
GET    /api/insights/headcount-by-country
GET    /api/insights/salary-by-department?country=IN
GET    /api/insights/attrition-by-country          (count of relieved employees per country from employee_history)
```

## Links

- **Live API:** *(placeholder — added after deployment)*
- **Live UI:** *(placeholder — added after deployment)*
- **Demo video:** *(placeholder — Loom link added after recording)*

## Docs

- [Architecture](docs/architecture.md)
- [Decisions](docs/decisions.md)
- [AI Usage Log](docs/ai-usage.md)
- [Deployment Guide](docs/DEPLOYMENT.md) *(added in Phase 5)*
