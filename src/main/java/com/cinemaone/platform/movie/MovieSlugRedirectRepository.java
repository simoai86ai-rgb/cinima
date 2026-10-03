package com.cinemaone.platform.movie;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieSlugRedirectRepository extends JpaRepository<MovieSlugRedirect, Long> {
    boolean existsByLocaleAndOldSlug(String locale, String oldSlug);

    @EntityGraph(attributePaths = {"movie", "movie.translations", "movie.genres", "movie.genres.translations"})
    Optional<MovieSlugRedirect> findByLocaleAndOldSlug(String locale, String oldSlug);
}
