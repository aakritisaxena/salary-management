# Architecture Decisions

- **Java 17 + Spring Boot 3.x:** Mainstream, well-understood stack. Spring Boot's auto-configuration minimises plumbing so the TDD cycle stays in focus. Spring Boot 3 requires Java 17+, which gives records, text blocks, and sealed classes.

- **H2 (file mode):** Zero infrastructure — the database is a single file alongside the project. Spring Boot's auto-configuration handles the dialect; tests use in-memory H2 with no extra setup. Acceptable for a single-writer scenario; swap to Postgres by changing the datasource URL and dialect in `application.yml`.

- **React + Vite + TypeScript:** Vite's near-instant HMR makes UI iteration fast. TypeScript catches contract mismatches between the frontend and the API at compile time, reducing runtime surprises.

- **UUID primary keys:** Avoids sequential ID collisions if records are ever merged across environments. Also produces opaque public identifiers — safer to expose in URLs than auto-increment integers, and compatible with client-generated IDs if needed later.

- **Currency field on Employee:** Salary figures are meaningless for cross-country comparison without knowing the unit. Storing an ISO 4217 currency code alongside the salary makes every record self-describing and prevents silent currency-mixing bugs in the insights layer.

- **Pagination (default size 50):** With 10,000 records, returning the full table in one request is slow and wasteful. Page-based pagination with a configurable size keeps P99 latency predictable and the frontend snappy.

- **Batch seeding via JDBC `batchUpdate`:** JPA `saveAll` issues one INSERT per row. Raw JDBC `batchUpdate` with pre-generated UUIDs and a single wrapping `TransactionTemplate` achieves significantly higher throughput — the single-transaction approach eliminates per-batch fsync overhead, which is the dominant cost at 10,000 rows. Memory footprint stays bounded to one batch (500 rows) at a time.

- **`ProblemDetail` error responses (RFC 9457):** Spring 6 ships built-in support; avoids a custom error envelope while staying standards-compliant. Field-level validation errors are embedded in the `properties` map.

- **Layer-based package structure:** Classes are organised by technical role (`model`, `repository`, `service`, `controller`, `dto`, `exception`, `common`) rather than by feature (`employee`, `insights`). Easier to navigate for anyone familiar with standard Spring layering, and every decision is defensible without needing to explain a custom convention.
