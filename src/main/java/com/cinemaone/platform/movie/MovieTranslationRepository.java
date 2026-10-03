package com.cinemaone.platform.movie;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieTranslationRepository extends JpaRepository<MovieTranslation, Long> {
    boolean existsByLocaleAndSlug(String locale, String slug);
    boolean existsByLocaleAndSlugAndMovie_IdNot(String locale, String slug, Long movieId);
}
