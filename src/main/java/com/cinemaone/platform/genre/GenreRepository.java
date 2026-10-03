package com.cinemaone.platform.genre;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    boolean existsBySlug(String slug);

    @EntityGraph(attributePaths = "translations")
    Optional<Genre> findBySlug(String slug);

    @Override
    @EntityGraph(attributePaths = "translations")
    List<Genre> findAll();
}
