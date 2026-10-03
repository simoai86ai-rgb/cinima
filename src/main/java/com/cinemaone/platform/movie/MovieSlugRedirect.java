package com.cinemaone.platform.movie;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "movie_slug_redirects",
       uniqueConstraints = @UniqueConstraint(name = "uk_movie_slug_redirect_locale_slug", columnNames = {"locale", "old_slug"}))
public class MovieSlugRedirect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(nullable = false, length = 16)
    private String locale;

    @Column(name = "old_slug", nullable = false, length = 320)
    private String oldSlug;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() { if (createdAt == null) createdAt = Instant.now(); }

    public Long getId() { return id; }
    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    public String getOldSlug() { return oldSlug; }
    public void setOldSlug(String oldSlug) { this.oldSlug = oldSlug; }
    public Instant getCreatedAt() { return createdAt; }
}
