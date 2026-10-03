# Cinema One - 3 phase delivery plan

## Phase 1 - Foundation and Admin - COMPLETE

Spring Boot, PostgreSQL, Flyway, Spring Security, Admin login, Movie/Genre CRUD, EN/FR/AR translation records and publication lifecycle.

## Phase 2 - Public Website, Search and SEO - COMPLETE

Cumulative on Phase 1:

- server-rendered responsive public UI
- `/en`, `/fr`, `/ar`
- Arabic RTL
- home/catalog/movie/genre/search/info pages
- real PostgreSQL data
- image references and fallbacks
- PostgreSQL trigram search
- dynamic canonical/hreflang/Open Graph
- JSON-LD
- sitemap + robots
- SEO-safe noindex handling for utility pages

## Phase 3 - Playback, Android and Production

Cumulative on Phase 2:

- playback source/session domain
- signed/temporary playback configuration
- Kotlin + Jetpack Compose Android app
- Media3 HLS/DASH playback
- verified Android App Links
- Play Integrity
- appropriate AdMob/IMA integration
- object storage image uploads
- monitoring, backup recovery, CI/CD and production deployment hardening
