CREATE TABLE admin_users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(320) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(160) NOT NULL,
    role VARCHAR(32) NOT NULL CHECK (role IN ('ADMIN')),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID NOT NULL UNIQUE,
    tmdb_id BIGINT UNIQUE,
    original_title VARCHAR(300) NOT NULL,
    original_language VARCHAR(16) NOT NULL,
    release_date DATE,
    runtime_minutes INTEGER CHECK (runtime_minutes IS NULL OR runtime_minutes > 0),
    vote_average NUMERIC(4,2) CHECK (vote_average IS NULL OR (vote_average >= 0 AND vote_average <= 10)),
    vote_count INTEGER CHECK (vote_count IS NULL OR vote_count >= 0),
    status VARCHAR(32) NOT NULL CHECK (status IN ('DRAFT','PUBLISHED','ARCHIVED')),
    published_at TIMESTAMPTZ,
    archived_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE movie_translations (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    locale VARCHAR(16) NOT NULL CHECK (locale IN ('en','fr','ar')),
    slug VARCHAR(320) NOT NULL,
    title VARCHAR(300) NOT NULL,
    description TEXT,
    short_description VARCHAR(500),
    seo_title VARCHAR(320),
    seo_description VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_movie_translation_movie_locale UNIQUE (movie_id, locale),
    CONSTRAINT uk_movie_translation_locale_slug UNIQUE (locale, slug)
);

CREATE TABLE genres (
    id BIGSERIAL PRIMARY KEY,
    slug VARCHAR(120) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE genre_translations (
    id BIGSERIAL PRIMARY KEY,
    genre_id BIGINT NOT NULL REFERENCES genres(id) ON DELETE CASCADE,
    locale VARCHAR(16) NOT NULL CHECK (locale IN ('en','fr','ar')),
    name VARCHAR(160) NOT NULL,
    CONSTRAINT uk_genre_translation_genre_locale UNIQUE (genre_id, locale)
);

CREATE TABLE movie_genres (
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    genre_id BIGINT NOT NULL REFERENCES genres(id) ON DELETE RESTRICT,
    PRIMARY KEY (movie_id, genre_id)
);

CREATE INDEX idx_movies_status_updated ON movies(status, updated_at DESC);
CREATE INDEX idx_movies_release_date ON movies(release_date DESC);
CREATE INDEX idx_movie_translations_movie ON movie_translations(movie_id);
CREATE INDEX idx_movie_translations_locale_title ON movie_translations(locale, title);
CREATE INDEX idx_movie_genres_genre_movie ON movie_genres(genre_id, movie_id);
