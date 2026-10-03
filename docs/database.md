# Database - Phase 2 additions

Phase 2 preserves all Phase 1 entities and adds two optional image reference columns to `movies`:

- `poster_url`
- `backdrop_url`

Flyway V3 also enables PostgreSQL `pg_trgm` and creates GIN trigram indexes for localized titles and original titles, plus lookup/published indexes.

Movie translation slugs remain unique per locale. Only `PUBLISHED` movies are returned by public catalog queries.

## Slug redirect history

Flyway V4 adds `movie_slug_redirects(movie_id, locale, old_slug, created_at)` with a unique `(locale, old_slug)` constraint. This protects old indexed URLs after editors change localized slugs.
