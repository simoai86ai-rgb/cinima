CREATE TABLE movie_slug_redirects (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    locale VARCHAR(16) NOT NULL CHECK (locale IN ('en','fr','ar')),
    old_slug VARCHAR(320) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_movie_slug_redirect_locale_slug UNIQUE (locale, old_slug)
);

CREATE INDEX idx_movie_slug_redirect_movie ON movie_slug_redirects(movie_id);
