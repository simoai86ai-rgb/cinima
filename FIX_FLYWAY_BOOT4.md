# Flyway startup fix for Spring Boot 4.1.1

## Symptom

Hibernate fails at startup with:

`Schema validation: missing table [admin_users]`

## Root cause

Spring Boot 4 modularized integrations that previously activated from third-party dependencies alone.
The project used `org.flywaydb:flyway-core` directly, which does not add Spring Boot 4's Flyway auto-configuration module.
As a result, Flyway migrations did not run before Hibernate schema validation.

## Fix applied

- Replaced `org.flywaydb:flyway-core` with `org.springframework.boot:spring-boot-starter-flyway`.
- Kept `org.flywaydb:flyway-database-postgresql` for PostgreSQL support.
- Explicitly set `spring.flyway.locations=classpath:db/migration`.
- Kept Hibernate at `ddl-auto: validate` so Flyway remains the only schema owner.

## First run

Start PostgreSQL, then start the application normally. Flyway should execute V1 through V4 before JPA validates the schema.

Expected tables include:

- `flyway_schema_history`
- `admin_users`
- `movies`
- `movie_translations`
- `genres`
- `genre_translations`
- `movie_genres`
- `movie_slug_redirects`

## If the database was partially created manually

Do not delete a database that contains data you need.

For a disposable local development database only, reset the Docker volume if migrations fail because tables already exist without Flyway history:

```bash
docker compose down -v
docker compose up -d
mvn spring-boot:run
```

This command deletes the local PostgreSQL Docker volume and all data in it.
