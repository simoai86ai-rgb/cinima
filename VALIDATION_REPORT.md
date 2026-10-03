# Cinema One Phase 2 - Validation Report

Date: 2026-10-03

## Completed checks

- `pom.xml` parsed successfully as XML.
- `application.yml`, `application-prod.yml`, and test YAML parsed successfully.
- `manifest.webmanifest` parsed successfully as JSON.
- 22 Thymeleaf/HTML templates inspected for nested forms; none found.
- 105 i18n keys are consistent across default, English, French, and Arabic bundles.
- 85 message keys referenced by templates resolve in the bundles.
- Literal static asset references checked; referenced files exist.
- JavaScript syntax checks passed for public, admin, and retained mock scripts.
- Dependency-free Java classes (`Slugifier`, `SiteLocale`) compile with Java 21.
- A parser-level `javac` scan over all Java sources found no Java syntax diagnostics. Framework symbols cannot resolve without the Maven dependency classpath, which is expected in this environment.
- Flyway migration sequence verified: V1, V2, V3, V4.
- Phase 2 remains cumulative/non-destructive over the Phase 1 schema.

## Maven limitation in this execution environment

The current build container does not have Maven installed, so a full `mvn test` / Spring context build could not be executed here. This is an environment limitation, not a reported project test failure.

Run these commands on a machine with Java 21 and Maven available before deployment:

```bash
mvn clean test
mvn clean package
```

Then run the application against PostgreSQL and perform the smoke checks below.

## Recommended smoke checks

1. Start PostgreSQL with `docker compose up -d`.
2. Run `mvn spring-boot:run`.
3. Verify `/en`, `/fr`, and `/ar`.
4. Verify Arabic RTL rendering.
5. Log in to `/admin`, create a movie, publish it, and verify it appears publicly.
6. Change a localized movie slug and verify the old URL returns HTTP 301 to the new URL.
7. Verify search, genre, year filtering, and pagination.
8. Inspect page source for canonical, hreflang, Open Graph, and JSON-LD.
9. Verify `/sitemap.xml`, `/sitemap-pages.xml`, `/sitemap-movies-1.xml`, and `/robots.txt`.
10. Run production with `SPRING_PROFILES_ACTIVE=prod`, a real `SITE_BASE_URL`, demo content disabled, and bootstrap admin disabled unless intentionally provisioning the first account.

## Phase 2 boundary

Protected playback sessions, Android Media3 playback, verified App Links, Play Integrity, ad integration, object-storage uploads, observability, backup automation, and deployment hardening remain Phase 3 work.

## Runtime patch - Thymeleaf home hero counter

- Replaced unsupported `#strings.padLeft(...)` with `#numbers.formatInteger(..., 2)`.
- Scanned all templates: no remaining `padLeft` calls.
- Added `PublicHomeTemplateTest` to render the homepage with a non-empty hero list and assert `01 / 01`.
- Disabled demo bootstrap in test configuration so context tests remain deterministic.
- Maven is not installed in the artifact-generation environment, so the new Maven test suite must be executed on the development machine with `mvn clean test`.
