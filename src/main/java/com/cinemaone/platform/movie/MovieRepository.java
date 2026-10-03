package com.cinemaone.platform.movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    long countByStatus(MovieStatus status);
    boolean existsByTmdbId(Long tmdbId);
    boolean existsByTmdbIdAndIdNot(Long tmdbId, Long id);

    @EntityGraph(attributePaths = {"translations", "genres", "genres.translations"})
    @Query("select distinct m from Movie m where m.id = :id")
    Optional<Movie> findDetailedById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"translations", "genres", "genres.translations"})
    @Query("select distinct m from Movie m where m.publicId = :publicId")
    Optional<Movie> findDetailedByPublicId(@Param("publicId") UUID publicId);

    @Query("""
        select distinct m from Movie m
        left join m.translations t
        where (:q is null or :q = '' or lower(m.originalTitle) like lower(concat('%', :q, '%')) or lower(t.title) like lower(concat('%', :q, '%')))
        order by m.updatedAt desc
        """)
    Page<Movie> search(@Param("q") String q, Pageable pageable);

    @Query("""
        select distinct m from Movie m
        join m.translations t
        left join m.genres g
        where m.status = :status
          and t.locale = :locale
          and (:q = '' or lower(t.title) like lower(concat('%', :q, '%')) or lower(m.originalTitle) like lower(concat('%', :q, '%')))
          and (:genreSlug = '' or g.slug = :genreSlug)
          and (:year is null or year(m.releaseDate) = :year)
        order by m.publishedAt desc, m.releaseDate desc, m.id desc
        """)
    Page<Movie> searchPublished(
        @Param("status") MovieStatus status,
        @Param("locale") String locale,
        @Param("q") String q,
        @Param("genreSlug") String genreSlug,
        @Param("year") Integer year,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {"translations", "genres", "genres.translations"})
    @Query("""
        select distinct m from Movie m
        join m.translations t
        where m.status = :status
          and t.locale = :locale
          and t.slug = :slug
        """)
    Optional<Movie> findPublishedByLocaleAndSlug(@Param("status") MovieStatus status, @Param("locale") String locale, @Param("slug") String slug);

    Page<Movie> findByStatusOrderByPublishedAtDesc(MovieStatus status, Pageable pageable);

    @Query("""
        select m from Movie m
        where m.status = :status
        order by case when m.voteCount is null then 1 else 0 end, m.voteCount desc,
                 case when m.voteAverage is null then 1 else 0 end, m.voteAverage desc, m.publishedAt desc
        """)
    Page<Movie> findTrending(@Param("status") MovieStatus status, Pageable pageable);

    Page<Movie> findByStatusOrderByIdAsc(MovieStatus status, Pageable pageable);

    @Query("select distinct year(m.releaseDate) from Movie m where m.status = :status and m.releaseDate is not null order by year(m.releaseDate) desc")
    List<Integer> findDistinctReleaseYears(@Param("status") MovieStatus status);
}
