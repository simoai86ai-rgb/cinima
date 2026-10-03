package com.cinemaone.platform.movie;

import com.cinemaone.platform.genre.Genre;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(name = "tmdb_id", unique = true)
    private Long tmdbId;

    @Column(name = "original_title", nullable = false, length = 300)
    private String originalTitle;

    @Column(name = "original_language", nullable = false, length = 16)
    private String originalLanguage = "en";

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "poster_url", length = 1000)
    private String posterUrl;

    @Column(name = "backdrop_url", length = 1000)
    private String backdropUrl;

    @Column(name = "runtime_minutes")
    private Integer runtimeMinutes;

    @Column(name = "vote_average", precision = 4, scale = 2)
    private BigDecimal voteAverage;

    @Column(name = "vote_count")
    private Integer voteCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private MovieStatus status = MovieStatus.DRAFT;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("locale ASC")
    private Set<MovieTranslation> translations = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "movie_genres",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @OrderBy("slug ASC")
    private Set<Genre> genres = new LinkedHashSet<>();

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (publicId == null) publicId = UUID.randomUUID();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public void addTranslation(MovieTranslation translation) {
        translations.add(translation);
        translation.setMovie(this);
    }

    public void clearTranslations() {
        translations.forEach(t -> t.setMovie(null));
        translations.clear();
    }

    public Long getId() { return id; }
    public UUID getPublicId() { return publicId; }
    public Long getTmdbId() { return tmdbId; }
    public void setTmdbId(Long tmdbId) { this.tmdbId = tmdbId; }
    public String getOriginalTitle() { return originalTitle; }
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }
    public String getOriginalLanguage() { return originalLanguage; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public String getBackdropUrl() { return backdropUrl; }
    public void setBackdropUrl(String backdropUrl) { this.backdropUrl = backdropUrl; }
    public Integer getRuntimeMinutes() { return runtimeMinutes; }
    public void setRuntimeMinutes(Integer runtimeMinutes) { this.runtimeMinutes = runtimeMinutes; }
    public BigDecimal getVoteAverage() { return voteAverage; }
    public void setVoteAverage(BigDecimal voteAverage) { this.voteAverage = voteAverage; }
    public Integer getVoteCount() { return voteCount; }
    public void setVoteCount(Integer voteCount) { this.voteCount = voteCount; }
    public MovieStatus getStatus() { return status; }
    public void setStatus(MovieStatus status) { this.status = status; }
    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getArchivedAt() { return archivedAt; }
    public void setArchivedAt(Instant archivedAt) { this.archivedAt = archivedAt; }
    public Long getVersion() { return version; }
    public Set<MovieTranslation> getTranslations() { return translations; }
    public String getEnglishTitle() { return translations.stream().filter(t -> "en".equals(t.getLocale())).map(MovieTranslation::getTitle).findFirst().orElse(originalTitle); }
    public Set<Genre> getGenres() { return genres; }
    public void setGenres(Set<Genre> genres) { this.genres = genres; }
}
