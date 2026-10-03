package com.cinemaone.platform.genre;

import jakarta.persistence.*;

@Entity
@Table(name = "genre_translations",
       uniqueConstraints = @UniqueConstraint(name = "uk_genre_translation_genre_locale", columnNames = {"genre_id", "locale"}))
public class GenreTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @Column(nullable = false, length = 16)
    private String locale;

    @Column(nullable = false, length = 160)
    private String name;

    public Long getId() { return id; }
    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
