# AI Usage Log

Tracks where Claude assisted during development, what was generated vs. reviewed, and what was verified independently. Maintained per Incubyte's transparency expectations.

---

| # | Area | What AI helped with | What I verified / changed |
|---|------|---------------------|--------------------------|
| 1 | Docs | Drafted `architecture.md`, `decisions.md`, and `README.md` structure | Reviewed all content; confirmed tech choices; edited currency field rationale |
| 2 | Backend setup | Generated `pom.xml` with H2, validation, and Spring Boot dependencies | Checked dependency versions; confirmed Spring Boot BOM manages versions |
| 3 | Backend setup | Scaffolded Spring Boot main class and `contextLoads` smoke test | Ran `./mvnw test` locally before committing |
| 4 | Domain model | Wrote tests then `Employee` and `EmployeeHistory` entity classes | Verified field constraints and UUID PK strategy; confirmed `@PrePersist` timestamps |
| 5 | Repository | Wrote `@DataJpaTest` tests for JPQL filter query (country, department, name LIKE) | Confirmed query handles null params correctly; tested edge cases locally |
| 6 | Service | Wrote `EmployeeService` unit tests with Mockito; implemented soft-delete via `EmployeeHistory` | Verified archive flow — record moves to history table, not hard deleted |
| 7 | Controller | Wrote `@WebMvcTest` tests for all CRUD endpoints; implemented `EmployeeController` | Checked RFC 9457 ProblemDetail responses for 404 and 409 cases |
| 8 | Seeder | Implemented JDBC `batchUpdate` seeder with `TransactionTemplate` (single transaction, 500-row chunks) | Timed locally; confirmed 10,000 rows insert under 2 seconds; reviewed memory footprint |
| 9 | Insights | Wrote aggregation query tests and `SalaryInsightsService`; implemented `InsightsController` | Verified department and country breakdowns return correct headcount and salary stats |
| 10 | Frontend | Scaffolded React 19 + Vite + TypeScript + Tailwind v4 + shadcn/ui project structure | Resolved Tailwind v4 plugin setup (`@tailwindcss/vite` not PostCSS); fixed TypeScript 6 path alias issue |
| 11 | Frontend | Built `EmployeeTable` with pagination, search, country/department filters, debounce | Tested search with 10,000 employees; verified debounce prevents excess API calls |
| 12 | Frontend | Built `EmployeeForm` (create/edit), `DeleteConfirmDialog`, `EmployeeDetailDialog` | Verified salary only visible in detail dialog (privacy UX); tested all CRUD flows end-to-end |
| 13 | Frontend | Built `InsightsPage` with stat cards, department bar chart, job-title drill-down table | Verified chart renders correctly; confirmed country selector scopes job-title data |
| 14 | Backend | Added structured SLF4J logging to `EmployeeService`, `GlobalExceptionHandler`, `SalaryInsightsService`, `DataSeeder` | Reviewed log levels (info/warn/error) match intent; confirmed no sensitive data logged |
| 15 | Deployment | Wrote `Dockerfile` (multi-stage, Maven image), `render.yaml`, `vercel.json`; made CORS configurable via env var | Fixed Docker context issue (Render using repo root); resolved CORS block by setting `APP_CORS_ALLOWED_ORIGINS` on Render |

