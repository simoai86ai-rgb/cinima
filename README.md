# Cinema One - Phase 2

Phase 2 turns the Phase 1 catalog/admin foundation into a real multilingual public website.

## Included

- Spring Boot 4.1.1 / Java 21
- PostgreSQL + Flyway migrations
- Existing Phase 1 Admin authentication + Movie/Genre CRUD
- Poster and backdrop references managed from Admin
- Server-rendered Thymeleaf public website
- English, French and Arabic routes
- RTL layout for Arabic
- Public home, catalog, genre, search, movie detail and legal/info pages
- PostgreSQL `pg_trgm` indexes for title search
- Dynamic canonical URLs and localized `hreflang`
- Dynamic Open Graph / Twitter metadata
- Movie + BreadcrumbList + ItemList JSON-LD
- Dynamic sitemap index + paged movie sitemaps + `robots.txt`
- `noindex` on internal search/filter/watch utility pages where appropriate
- Permanent 301 redirects when a localized movie slug changes
- Responsive cinema UI based on the approved V3 mock
- Optional fictional demo catalog in development only

## Run locally

1. Start PostgreSQL:

```bash
docker compose up -d
```

2. Copy `.env.example` values into your environment or IDE run configuration.

3. Run with Maven:

```bash
mvn spring-boot:run
```

4. Open:

- Public EN: `http://localhost:8080/en`
- Public FR: `http://localhost:8080/fr`
- Public AR: `http://localhost:8080/ar`
- Admin: `http://localhost:8080/admin`
- Sitemap: `http://localhost:8080/sitemap.xml`
- Robots: `http://localhost:8080/robots.txt`

### Default development admin

When running the default local profile with no admin environment variables set:

- Email: `admin@cinemaone.local`
- Password: `ChangeMe123!`

The development bootstrap can synchronize this password for the default admin when `APP_ADMIN_RESET_PASSWORD=true`. The production profile disables bootstrap by default and never resets an existing password. Never use these development credentials in production.

## Demo catalog

`APP_DEMO_CONTENT_ENABLED=true` seeds fictional movies only when the movies table is empty. Production profile disables demo content.

## Images

Phase 2 stores image references (`poster_url`, `backdrop_url`) instead of uploading binary image files. Admin values must use either:

- local paths beginning with `/`, or
- `https://` URLs.

Object storage upload and image processing are intentionally deferred to Phase 3.

## SEO domain

Set the real public domain before deployment:

```text
SITE_BASE_URL=https://www.example.com
```

Canonical links, hreflang links, Open Graph URLs, sitemap URLs and JSON-LD are generated from this setting.

## Database upgrade

Existing Phase 1 databases upgrade through:

- `V3__phase2_public_catalog.sql`
- `V4__movie_slug_redirects.sql`

The migration adds poster/backdrop references and PostgreSQL trigram search indexes.

## Verification

The project includes smoke tests for:

- all EN / FR / AR public pages
- Home, Movies, Search, Genres, Genre, Movie Detail and Watch
- About, Contact, Privacy and Terms
- localized 404 handling
- Admin Login, Dashboard, Movies, Add/Edit Movie and Genres
- default development admin authentication
- sitemap and robots endpoints
- Thymeleaf safety guard against combining `th:each` and `th:replace` on the same element

Run the full verification locally with:

```bash
mvn clean test
```

## Next: Phase 3

Phase 3 will add the protected playback/session API, Android Kotlin/Compose + Media3 player, verified App Links, Play Integrity, ad integration and production hardening.

## GitHub -> AWS automatic deployment

This build now includes a production CI/CD pipeline for Linux EC2:

- Workflow: `.github/workflows/deploy-main-to-aws.yml`
- Docker image: `Dockerfile`
- Remote deployment + rollback: `deploy/aws/deploy-release.sh`
- One-time EC2 prerequisites: `deploy/aws/bootstrap-ec2.sh`
- AWS/GitHub values checklist: `docs/AWS_CICD_REQUIRED_VALUES.md`
- GitHub environment variables example: `deploy/aws/github-environment-variables.example`
- IAM policy templates: `deploy/aws/iam/`

Every push/merge to `main` runs `mvn clean verify`. Only a successful build is pushed to Amazon ECR and deployed to EC2 through AWS Systems Manager. The deployed image is pinned to the exact Git commit SHA. The container exposes `/actuator/health`; a failed health check triggers an automatic rollback to the previous image.

The workflow authenticates GitHub to AWS with OIDC, so permanent AWS access keys are not required in GitHub Secrets.

## CI test dependencies (Spring Boot 4)

This project uses Spring Boot 4 focused test starters. In particular, `@WebMvcTest` and `@AutoConfigureMockMvc` require `spring-boot-starter-webmvc-test`. Full-context tests use the isolated H2 `test` profile, so GitHub Actions does not require a PostgreSQL service for `mvn clean verify`. See `TEST_CI_FIX.md`.
