package com.cinemaone.platform.publicsite;

import com.cinemaone.platform.genre.Genre;
import com.cinemaone.platform.genre.GenreRepository;
import com.cinemaone.platform.genre.GenreTranslation;
import com.cinemaone.platform.movie.Movie;
import com.cinemaone.platform.movie.MovieRepository;
import com.cinemaone.platform.movie.MovieStatus;
import com.cinemaone.platform.movie.MovieSlugRedirectRepository;
import com.cinemaone.platform.movie.MovieTranslation;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PublicCatalogService {
    private static final String DEFAULT_POSTER = "/assets/img/posters/fallback-poster.svg";
    private static final String DEFAULT_BACKDROP = "/assets/img/backdrops/fallback-backdrop.svg";

    private final MovieRepository movieRepository;
    private final MovieSlugRedirectRepository slugRedirectRepository;
    private final GenreRepository genreRepository;

    public PublicCatalogService(MovieRepository movieRepository, MovieSlugRedirectRepository slugRedirectRepository, GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.slugRedirectRepository = slugRedirectRepository;
        this.genreRepository = genreRepository;
    }

    public List<MovieCardView> latest(String locale, int limit) {
        return movieRepository.findByStatusOrderByPublishedAtDesc(MovieStatus.PUBLISHED, PageRequest.of(0, clamp(limit, 1, 30)))
            .getContent().stream().map(movie -> toCard(movie, locale)).toList();
    }

    public List<MovieCardView> trending(String locale, int limit) {
        return movieRepository.findTrending(MovieStatus.PUBLISHED, PageRequest.of(0, clamp(limit, 1, 30)))
            .getContent().stream().map(movie -> toCard(movie, locale)).toList();
    }

    public CatalogPageView search(String locale, String query, String genreSlug, Integer year, int page, int size) {
        String q = query == null ? "" : query.trim();
        String genre = genreSlug == null ? "" : genreSlug.trim();
        Page<Movie> result = movieRepository.searchPublished(MovieStatus.PUBLISHED, locale, q, genre, year, PageRequest.of(Math.max(page, 0), clamp(size, 1, 48)));
        return new CatalogPageView(
            result.getContent().stream().map(movie -> toCard(movie, locale)).toList(),
            result.getNumber(), result.getTotalPages(), result.getTotalElements(), result.hasPrevious(), result.hasNext());
    }

    public MovieRouteResolution resolveMovieRoute(String locale, String slug) {
        var current = movieRepository.findPublishedByLocaleAndSlug(MovieStatus.PUBLISHED, locale, slug);
        if (current.isPresent()) return new MovieRouteResolution(toDetail(current.get(), locale), null);

        var redirect = slugRedirectRepository.findByLocaleAndOldSlug(locale, slug)
            .filter(item -> item.getMovie().getStatus() == MovieStatus.PUBLISHED)
            .orElseThrow(() -> new EntityNotFoundException("Movie not found"));
        MovieDetailView movie = toDetail(redirect.getMovie(), locale);
        return new MovieRouteResolution(movie, movie.detailUrl());
    }

    public MovieDetailView detailByPublicId(String locale, UUID publicId) {
        Movie movie = movieRepository.findDetailedByPublicId(publicId)
            .filter(item -> item.getStatus() == MovieStatus.PUBLISHED)
            .orElseThrow(() -> new EntityNotFoundException("Movie not found"));
        return toDetail(movie, locale);
    }

    public List<MovieCardView> related(String locale, MovieDetailView current, int limit) {
        String genre = current.genres().isEmpty() ? "" : current.genres().getFirst().slug();
        return search(locale, "", genre, null, 0, Math.max(limit + 2, 8)).movies().stream()
            .filter(movie -> !movie.publicId().equals(current.publicId()))
            .limit(limit)
            .toList();
    }

    public List<GenreView> genres(String locale) {
        return genreRepository.findAll().stream()
            .map(genre -> new GenreView(genre.getSlug(), genreName(genre, locale), "/" + locale + "/genre/" + genre.getSlug()))
            .sorted(Comparator.comparing(GenreView::name, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    public GenreView genre(String locale, String slug) {
        Genre genre = genreRepository.findBySlug(slug)
            .orElseThrow(() -> new EntityNotFoundException("Genre not found"));
        return new GenreView(genre.getSlug(), genreName(genre, locale), "/" + locale + "/genre/" + genre.getSlug());
    }

    public List<Integer> availableYears() {
        return movieRepository.findDistinctReleaseYears(MovieStatus.PUBLISHED);
    }

    public long publishedCount() {
        return movieRepository.countByStatus(MovieStatus.PUBLISHED);
    }

    public List<SitemapMovieView> sitemapMovies(int page, int size) {
        return movieRepository.findByStatusOrderByIdAsc(MovieStatus.PUBLISHED, PageRequest.of(Math.max(page, 0), clamp(size, 1, 15000)))
            .getContent().stream()
            .map(movie -> {
                Map<String, String> paths = new LinkedHashMap<>();
                for (MovieTranslation item : movie.getTranslations()) {
                    paths.put(item.getLocale(), "/" + item.getLocale() + "/movie/" + item.getSlug());
                }
                return new SitemapMovieView(movie.getUpdatedAt(), paths);
            }).toList();
    }

    private MovieCardView toCard(Movie movie, String locale) {
        MovieTranslation translation = translation(movie, locale);
        String detailUrl = "/" + locale + "/movie/" + translation.getSlug();
        return new MovieCardView(
            movie.getPublicId(), translation.getSlug(), translation.getTitle(), firstNonBlank(translation.getShortDescription(), translation.getDescription()),
            firstNonBlank(movie.getPosterUrl(), DEFAULT_POSTER), firstNonBlank(movie.getBackdropUrl(), DEFAULT_BACKDROP),
            movie.getReleaseDate() == null ? null : movie.getReleaseDate().getYear(), runtime(movie.getRuntimeMinutes()), rating(movie.getVoteAverage()),
            movie.getGenres().stream().findFirst().map(genre -> genreName(genre, locale)).orElse("Movie"), detailUrl,
            "/watch/" + movie.getPublicId() + "?lang=" + locale
        );
    }

    private MovieDetailView toDetail(Movie movie, String locale) {
        MovieTranslation translation = translation(movie, locale);
        List<GenreView> genres = movie.getGenres().stream()
            .map(genre -> new GenreView(genre.getSlug(), genreName(genre, locale), "/" + locale + "/genre/" + genre.getSlug()))
            .toList();
        Map<String, String> localized = new LinkedHashMap<>();
        for (MovieTranslation item : movie.getTranslations()) {
            localized.put(item.getLocale(), "/" + item.getLocale() + "/movie/" + item.getSlug());
        }
        String title = translation.getTitle();
        String shortDescription = firstNonBlank(translation.getShortDescription(), translation.getDescription());
        String description = firstNonBlank(translation.getDescription(), translation.getShortDescription());
        return new MovieDetailView(
            movie.getPublicId(), translation.getSlug(), title, description, shortDescription,
            firstNonBlank(movie.getPosterUrl(), DEFAULT_POSTER), firstNonBlank(movie.getBackdropUrl(), DEFAULT_BACKDROP),
            movie.getReleaseDate() == null ? null : movie.getReleaseDate().getYear(),
            movie.getReleaseDate() == null ? "" : movie.getReleaseDate().format(DateTimeFormatter.ISO_DATE),
            runtime(movie.getRuntimeMinutes()), rating(movie.getVoteAverage()), movie.getOriginalLanguage(), genres,
            "/" + locale + "/movie/" + translation.getSlug(), "/watch/" + movie.getPublicId() + "?lang=" + locale,
            localized, firstNonBlank(translation.getSeoTitle(), title + " - Cinema One"),
            firstNonBlank(translation.getSeoDescription(), shortDescription)
        );
    }

    private MovieTranslation translation(Movie movie, String locale) {
        return movie.getTranslations().stream().filter(item -> locale.equals(item.getLocale())).findFirst()
            .or(() -> movie.getTranslations().stream().filter(item -> "en".equals(item.getLocale())).findFirst())
            .orElseThrow(() -> new EntityNotFoundException("Movie translation not found"));
    }

    private String genreName(Genre genre, String locale) {
        return genre.getTranslations().stream().filter(item -> locale.equals(item.getLocale())).map(GenreTranslation::getName).findFirst()
            .or(() -> genre.getTranslations().stream().filter(item -> "en".equals(item.getLocale())).map(GenreTranslation::getName).findFirst())
            .orElse(genre.getSlug());
    }

    private static String runtime(Integer minutes) {
        if (minutes == null || minutes <= 0) return "";
        int hours = minutes / 60;
        int mins = minutes % 60;
        return hours > 0 ? hours + "h " + mins + "m" : mins + "m";
    }

    private static String rating(BigDecimal rating) {
        return rating == null ? "—" : String.format(java.util.Locale.ROOT, "%.1f", rating.doubleValue());
    }

    private static int clamp(int value, int min, int max) { return Math.max(min, Math.min(value, max)); }

    private static String firstNonBlank(String... values) {
        for (String value : values) if (value != null && !value.isBlank()) return value;
        return "";
    }
}
