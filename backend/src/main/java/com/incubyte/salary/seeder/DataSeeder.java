package com.incubyte.salary.seeder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class DataSeeder {

    public static final int SEED_COUNT = 10_000;
    private static final int BATCH_SIZE = 500;

    private static final String[] COUNTRIES = {"IN", "US", "GB", "DE", "CA", "AU", "SG", "JP"};
    private static final String[] DEPARTMENTS = {"Engineering", "Product", "Design", "Marketing", "Sales", "Finance", "HR", "Operations"};
    private static final String[] JOB_TITLES = {"Software Engineer", "Senior Engineer", "Product Manager", "Designer", "Analyst", "Manager", "Director", "Lead Engineer"};
    private static final String[][] COUNTRY_CURRENCY = {
            {"IN", "INR"}, {"US", "USD"}, {"GB", "GBP"}, {"DE", "EUR"},
            {"CA", "CAD"}, {"AU", "AUD"}, {"SG", "SGD"}, {"JP", "JPY"}
    };

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final boolean seedEnabled;

    public DataSeeder(JdbcTemplate jdbcTemplate,
                      TransactionTemplate transactionTemplate,
                      @Value("${app.seed.enabled:false}") boolean seedEnabled) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
        this.seedEnabled = seedEnabled;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (seedEnabled) {
            seed();
        }
    }

    public void seed() {
        Integer existing = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM employees", Integer.class);
        if (existing != null && existing >= SEED_COUNT) {
            return;
        }

        List<String> firstNames = loadLines("data/first_names.txt");
        List<String> lastNames = loadLines("data/last_names.txt");

        Random random = new Random(42);
        long nowMillis = Instant.now().toEpochMilli();

        String sql = "INSERT INTO employees (id, full_name, job_title, country, salary, currency, email, department, hire_date, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // single transaction across all chunks: one fsync, no partial-seed state on failure
        transactionTemplate.executeWithoutResult(status -> {
            for (int start = 0; start < SEED_COUNT; start += BATCH_SIZE) {
                final int from = start;
                final int count = Math.min(BATCH_SIZE, SEED_COUNT - from);
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        String firstName = firstNames.get(random.nextInt(firstNames.size()));
                        String lastName = lastNames.get(random.nextInt(lastNames.size()));
                        String[] cc = COUNTRY_CURRENCY[random.nextInt(COUNTRY_CURRENCY.length)];
                        LocalDate hireDate = LocalDate.now().minusDays(random.nextInt(3650));
                        ps.setString(1, UUID.randomUUID().toString());
                        ps.setString(2, firstName + " " + lastName);
                        ps.setString(3, JOB_TITLES[random.nextInt(JOB_TITLES.length)]);
                        ps.setString(4, cc[0]);
                        ps.setBigDecimal(5, BigDecimal.valueOf(30000 + random.nextInt(120000)));
                        ps.setString(6, cc[1]);
                        ps.setString(7, firstName.toLowerCase() + "." + lastName.toLowerCase() + (from + i) + "@example.com");
                        ps.setString(8, DEPARTMENTS[random.nextInt(DEPARTMENTS.length)]);
                        ps.setTimestamp(9, Timestamp.valueOf(hireDate.atStartOfDay()));
                        ps.setLong(10, nowMillis);
                        ps.setLong(11, nowMillis);
                    }

                    @Override
                    public int getBatchSize() {
                        return count;
                    }
                });
            }
        });
    }

    private List<String> loadLines(String resourcePath) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(resourcePath).getInputStream()))) {
            return reader.lines().filter(l -> !l.isBlank()).toList();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load resource: " + resourcePath, e);
        }
    }
}
