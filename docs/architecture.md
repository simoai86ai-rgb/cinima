# Architecture - Phase 2

```text
Browser / Search crawler
        |
        v
Spring MVC Controllers
        |
        +-- Thymeleaf SSR public site
        +-- Admin SSR forms
        +-- SEO / sitemap endpoints
        |
Application services
        |
        +-- MovieService (admin/write)
        +-- PublicCatalogService (public/read)
        +-- SeoService
        |
Spring Data JPA
        |
PostgreSQL + Flyway
```

The public website does not call its own REST API. Controllers invoke the shared service layer directly. The future Android client will use dedicated versioned APIs in Phase 3.
