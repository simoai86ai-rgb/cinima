package com.cinemaone.platform.movie;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MovieForm {
    private Long id;

    @NotBlank
    @Size(max = 300)
    private String originalTitle;

    @NotBlank
    @Size(max = 16)
    private String originalLanguage = "en";

    @Positive
    private Long tmdbId;

    private LocalDate releaseDate;

    @Size(max = 1000)
    private String posterUrl;

    @Size(max = 1000)
    private String backdropUrl;

    @Min(1)
    @Max(1000)
    private Integer runtimeMinutes;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private BigDecimal voteAverage;

    @Min(0)
    private Integer voteCount;

    @NotNull
    private MovieStatus status = MovieStatus.DRAFT;

    private List<Long> genreIds = new ArrayList<>();

    @Valid
    @Size(min = 3, max = 3)
    private List<TranslationInput> translations = defaultTranslations();

    public static MovieForm from(Movie movie) {
        MovieForm form = new MovieForm();
        form.id = movie.getId();
        form.originalTitle = movie.getOriginalTitle();
        form.originalLanguage = movie.getOriginalLanguage();
        form.tmdbId = movie.getTmdbId();
        form.releaseDate = movie.getReleaseDate();
        form.posterUrl = movie.getPosterUrl();
        form.backdropUrl = movie.getBackdropUrl();
        form.runtimeMinutes = movie.getRuntimeMinutes();
        form.voteAverage = movie.getVoteAverage();
        form.voteCount = movie.getVoteCount();
        form.status = movie.getStatus();
        form.genreIds = movie.getGenres().stream().map(g -> g.getId()).toList();

        Map<String, MovieTranslation> byLocale = movie.getTranslations().stream()
            .collect(Collectors.toMap(MovieTranslation::getLocale, t -> t));
        form.translations = new ArrayList<>();
        for (String locale : List.of("en", "fr", "ar")) {
            MovieTranslation t = byLocale.get(locale);
            form.translations.add(t == null ? new TranslationInput(locale) : TranslationInput.from(t));
        }
        return form;
    }

    private static List<TranslationInput> defaultTranslations() {
        return new ArrayList<>(List.of(new TranslationInput("en"), new TranslationInput("fr"), new TranslationInput("ar")));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOriginalTitle() { return originalTitle; }
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }
    public String getOriginalLanguage() { return originalLanguage; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }
    public Long getTmdbId() { return tmdbId; }
    public void setTmdbId(Long tmdbId) { this.tmdbId = tmdbId; }
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
    public List<Long> getGenreIds() { return genreIds; }
    public void setGenreIds(List<Long> genreIds) { this.genreIds = genreIds == null ? new ArrayList<>() : genreIds; }
    public List<TranslationInput> getTranslations() { return translations; }
    public void setTranslations(List<TranslationInput> translations) { this.translations = translations; }

    public static class TranslationInput {
        @NotBlank
        @Pattern(regexp = "en|fr|ar")
        private String locale;

        @NotBlank
        @Size(max = 300)
        private String title;

        @Size(max = 320)
        private String slug;

        @Size(max = 500)
        private String shortDescription;

        @Size(max = 10000)
        private String description;

        @Size(max = 320)
        private String seoTitle;

        @Size(max = 500)
        private String seoDescription;

        public TranslationInput() {}
        public TranslationInput(String locale) { this.locale = locale; }

        static TranslationInput from(MovieTranslation t) {
            TranslationInput input = new TranslationInput(t.getLocale());
            input.title = t.getTitle();
            input.slug = t.getSlug();
            input.description = t.getDescription();
            input.shortDescription = t.getShortDescription();
            input.seoTitle = t.getSeoTitle();
            input.seoDescription = t.getSeoDescription();
            return input;
        }

        public String getLocale() { return locale; }
        public void setLocale(String locale) { this.locale = locale; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
        public String getShortDescription() { return shortDescription; }
        public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSeoTitle() { return seoTitle; }
        public void setSeoTitle(String seoTitle) { this.seoTitle = seoTitle; }
        public String getSeoDescription() { return seoDescription; }
        public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }
    }
}
