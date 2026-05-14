# Architecture

## Problem Statement

A web application for managing employee salary data across multiple countries and departments. Supports full CRUD on employee records and provides salary insights (statistics, percentile distributions, cross-department comparisons) via a REST API consumed by a React SPA.

## Components

```
┌──────────────────────┐          ┌──────────────────────────────┐
│   React SPA          │ ──HTTP──▶│   Spring Boot REST API       │
│   Vite + TS          │          │   Java 17, Port 8080         │
│   Hosted: Vercel     │◀──JSON───│   Hosted: Render             │
└──────────────────────┘          └──────────────┬───────────────┘
                                                 │  JPA (schema)
                                                 │  JDBC (seed)
                                                 ▼
                                       ┌─────────────────────┐
                                       │   H2 Database       │
                                       │   salary.mv.db      │
                                       └─────────────────────┘
```

## Domain Model

`Employee` entity (active employees only):
- `id` (UUID, primary key)
- `fullName` (String, required, max 200)
- `jobTitle` (String, required, max 100)
- `country` (String, required, max 100)
- `salary` (BigDecimal, required, must be >= 0)
- `currency` (String, required, ISO 4217 code, 3 chars)
- `email` (String, required, valid email, unique)
- `department` (String, required, max 100)
- `hireDate` (LocalDate, required, cannot be in future)
- `createdAt` (Instant, auto)
- `updatedAt` (Instant, auto)

`EmployeeHistory` entity (archived/relieved employees):
- Same fields as Employee, plus:
- `employeeId` (UUID — the original employee ID, preserved)
- `relievedAt` (Instant — when they were archived)
- `createdAt` (Instant, auto)

**Important:** `DELETE /api/employees/{id}` does NOT hard delete. It copies the record to `employee_history` with `relievedAt = now()`, then removes from `employees`. All insight queries run only on the `employees` table (no status filtering needed).

## Key Non-Functional Concerns

- **10,000 employees:** Seeding uses JDBC batch inserts in a single transaction. 10,000 rows insert in under 2 seconds.
- **Pagination:** Default page size 50. All list endpoints are paginated so payload size stays predictable regardless of row count.
- **H2 file mode:** Fast, zero-infrastructure database. File persists across restarts; tests use in-memory mode with the same H2 dialect — no dialect switching needed. Trade-off: no concurrent writers, acceptable for single-user assignment scope.
- **Stateless API:** No server-side sessions; suitable for horizontal scale-out if the DB is later swapped for Postgres (one datasource URL change).
- **Validation layering:** Jakarta Bean Validation on the entity, enforced by `@Valid` at the controller, surfaced as RFC 9457 `ProblemDetail` error responses.
