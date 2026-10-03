ALTER TABLE movies
    ADD COLUMN poster_url VARCHAR(1000),
    ADD COLUMN backdrop_url VARCHAR(1000);

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_movies_original_title_trgm
    ON movies USING GIN (lower(original_title) gin_trgm_ops);

CREATE INDEX idx_movie_translations_title_trgm
    ON movie_translations USING GIN (lower(title) gin_trgm_ops);

CREATE INDEX idx_movie_translations_slug_lookup
    ON movie_translations(locale, slug);

CREATE INDEX idx_movies_public_published
    ON movies(status, published_at DESC, release_date DESC);
