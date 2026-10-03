# Cinema One Phase 2 - QA Report

## Fixed issues

1. Spring Boot 4 Flyway integration uses `spring-boot-starter-flyway` + PostgreSQL Flyway module.
2. Removed unsupported Thymeleaf `#strings.padLeft(...)`; hero numbering uses `#numbers.formatInteger(...)`.
3. Fixed Thymeleaf processor-precedence errors by never combining `th:each` and `th:replace` on the same element.
   - Home trending cards
   - Home latest cards
   - Home recommended cards
   - Movies catalog cards
   - Search result cards
   - Genre result cards
   - Related movie cards
4. Development admin bootstrap now has known defaults and can synchronize an existing local admin password.
5. Production explicitly disables password synchronization and requires explicit credentials if bootstrap is manually enabled.
6. `robots.txt` blocks both `/admin` and `/admin/`.

## Default development admin

- Email: `admin@cinemaone.local`
- Password: `ChangeMe123!`

These defaults are for local development only.

## Static QA completed in the build environment

- 22 HTML templates parsed successfully.
- 105 i18n keys are identical across `messages_en`, `messages_fr`, `messages_ar`, and the default bundle.
- All message keys referenced by public templates exist.
- All fragment calls resolve to declared fragments.
- No template contains `#strings.padLeft`.
- No element combines `th:each` with `th:replace` / `th:insert` / `th:include`.
- `application.yml`, `application-prod.yml`, test YAML, and `docker-compose.yml` parse successfully.
- `pom.xml` parses successfully.
- `manifest.webmanifest` parses successfully.
- Flyway migrations are contiguous: V1, V2, V3, V4.
- Local static assets referenced by templates and Java bootstrap data exist.
- JavaScript syntax checks pass for `public.js`, `phase1.js`, and `mock-app.js`.

## Runtime smoke tests included

The Maven test suite now contains coverage for:

### Public routes

- `/en`, `/fr`, `/ar`
- `/{locale}/movies`
- `/{locale}/search`
- `/{locale}/genres`
- `/{locale}/genre/{slug}`
- `/{locale}/movie/{slug}`
- `/watch/{publicId}`
- `/{locale}/about`
- `/{locale}/contact`
- `/{locale}/privacy`
- `/{locale}/terms`
- localized 404 handling
- root and legacy redirects

### Admin routes

- `/admin/login`
- `/admin`
- `/admin/movies`
- `/admin/movies/new`
- `/admin/movies/{id}/edit`
- `/admin/genres`

### Security

- Default local admin authentication with `admin@cinemaone.local / ChangeMe123!`
- Authenticated dashboard access after login

### SEO utility routes

- `/sitemap.xml`
- `/sitemap-pages.xml`
- `/sitemap-movies-1.xml`
- `/robots.txt`

### Regression guards

- A template test fails if `th:each` and fragment replacement are added to the same element again.
- A template test fails if unsupported `#strings.padLeft` is reintroduced.

## Required local runtime verification

This execution environment does not contain Maven or network access to Maven Central, so the Maven/Spring runtime suite cannot be executed here. Run this on the development machine before accepting the build:

```bash
mvn clean test
```

Only start the app if all tests pass:

```bash
mvn spring-boot:run
```

If using IntelliJ, delete/rebuild the old `target` output or run `mvn clean` so stale templates/classes from a previous ZIP are not reused.
