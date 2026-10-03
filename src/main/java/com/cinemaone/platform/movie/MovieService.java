package com.cinemaone.platform.movie;

import com.cinemaone.platform.common.Slugifier;
import com.cinemaone.platform.genre.Genre;
import com.cinemaone.platform.genre.GenreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieTranslationRepository translationRepository;
    private final MovieSlugRedirectRepository slugRedirectRepository;
    private final GenreRepository genreRepository;

    public MovieService(MovieRepository movieRepository, MovieTranslationRepository translationRepository, MovieSlugRedirectRepository slugRedirectRepository, GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.translationRepository = translationRepository;
        this.slugRedirectRepository = slugRedirectRepository;
        this.genreRepository = genreRepository;
    }

    public Page<Movie> search(String query, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        return movieRepository.search(query == null ? "" : query.trim(), pageable);
    }

    public Movie get(Long id) {
        return movieRepository.findDetailedById(id)
            .orElseThrow(() -> new EntityNotFoundException("Movie not found"));
    }

    public long countAll() { return movieRepository.count(); }
    public long countPublished() { return movieRepository.countByStatus(MovieStatus.PUBLISHED); }
    public long countDrafts() { return movieRepository.countByStatus(MovieStatus.DRAFT); }
    public long countArchived() { return movieRepository.countByStatus(MovieStatus.ARCHIVED); }

    @Transactional
    public Movie create(MovieForm form) {
        Movie movie = new Movie();
        apply(movie, form);
        return movieRepository.save(movie);
    }

    @Transactional
    public Movie update(Long id, MovieForm form) {
        Movie movie = get(id);
        apply(movie, form);
        return movieRepository.save(movie);
    }

    @Transactional
    public void delete(Long id) {
        if (!movieRepository.existsById(id)) throw new EntityNotFoundException("Movie not found");
        movieRepository.deleteById(id);
    }

    @Transactional
    public void setStatus(Long id, MovieStatus status) {
        Movie movie = get(id);
        updateStatusTimestamps(movie, status);
        movie.setStatus(status);
    }

    private void apply(Movie movie, MovieForm form) {
        movie.setOriginalTitle(form.getOriginalTitle().trim());
        movie.setOriginalLanguage(form.getOriginalLanguage().trim().toLowerCase());
        if (form.getTmdbId() != null) {
            boolean duplicateTmdb = movie.getId() == null
                ? movieRepository.existsByTmdbId(form.getTmdbId())
                : movieRepository.existsByTmdbIdAndIdNot(form.getTmdbId(), movie.getId());
            if (duplicateTmdb) throw new IllegalArgumentException("TMDB ID is already assigned to another movie.");
        }
        movie.setTmdbId(form.getTmdbId());
        movie.setReleaseDate(form.getReleaseDate());
        movie.setPosterUrl(normalizeImageUrl(form.getPosterUrl(), "Poster URL"));
        movie.setBackdropUrl(normalizeImageUrl(form.getBackdropUrl(), "Backdrop URL"));
        movie.setRuntimeMinutes(form.getRuntimeMinutes());
        movie.setVoteAverage(form.getVoteAverage());
        movie.setVoteCount(form.getVoteCount());
        updateStatusTimestamps(movie, form.getStatus());
        movie.setStatus(form.getStatus());

        List<Long> genreIds = form.getGenreIds() == null ? List.of() : form.getGenreIds().stream().distinct().toList();
        List<Genre> genres = genreRepository.findAllById(genreIds);
        if (genres.size() != genreIds.size()) throw new IllegalArgumentException("One or more selected genres do not exist.");
        movie.getGenres().clear();
        movie.getGenres().addAll(new LinkedHashSet<>(genres));

        validateAndApplyTranslations(movie, form.getTranslations());
    }

    private void validateAndApplyTranslations(Movie movie, List<MovieForm.TranslationInput> inputs) {
        if (inputs == null || inputs.size() != 3) throw new IllegalArgumentException("English, French and Arabic translations are required.");
        long localeCount = inputs.stream().map(MovieForm.TranslationInput::getLocale).distinct().count();
        if (localeCount != 3 || !inputs.stream().map(MovieForm.TranslationInput::getLocale).allMatch(l -> List.of("en", "fr", "ar").contains(l))) {
            throw new IllegalArgumentException("Translations must contain en, fr and ar exactly once.");
        }

        for (MovieForm.TranslationInput input : inputs) {
            String slug = (input.getSlug() == null || input.getSlug().isBlank())
                ? Slugifier.slugify(input.getTitle())
                : Slugifier.slugify(input.getSlug());
            boolean duplicate = movie.getId() == null
                ? translationRepository.existsByLocaleAndSlug(input.getLocale(), slug)
                : translationRepository.existsByLocaleAndSlugAndMovie_IdNot(input.getLocale(), slug, movie.getId());
            if (duplicate) throw new IllegalArgumentException("Slug '" + slug + "' already exists for locale " + input.getLocale() + ".");
            if (slugRedirectRepository.existsByLocaleAndOldSlug(input.getLocale(), slug)) {
                throw new IllegalArgumentException("Slug '" + slug + "' is reserved by a previous movie URL for locale " + input.getLocale() + ".");
            }
        }

        Map<String, MovieTranslation> existing = movie.getTranslations().stream()
            .collect(Collectors.toMap(MovieTranslation::getLocale, Function.identity()));
        for (MovieForm.TranslationInput input : inputs) {
            MovieTranslation translation = existing.get(input.getLocale());
            String newSlug = (input.getSlug() == null || input.getSlug().isBlank()) ? Slugifier.slugify(input.getTitle()) : Slugifier.slugify(input.getSlug());
            if (translation == null) {
                translation = new MovieTranslation();
                translation.setLocale(input.getLocale());
                movie.addTranslation(translation);
            } else if (translation.getSlug() != null && !translation.getSlug().equals(newSlug)) {
                MovieSlugRedirect redirect = new MovieSlugRedirect();
                redirect.setMovie(movie);
                redirect.setLocale(input.getLocale());
                redirect.setOldSlug(translation.getSlug());
                slugRedirectRepository.save(redirect);
            }
            translation.setTitle(input.getTitle().trim());
            translation.setSlug(newSlug);
            translation.setDescription(blankToNull(input.getDescription()));
            translation.setShortDescription(blankToNull(input.getShortDescription()));
            translation.setSeoTitle(blankToNull(input.getSeoTitle()));
            translation.setSeoDescription(blankToNull(input.getSeoDescription()));
        }
    }

    private void updateStatusTimestamps(Movie movie, MovieStatus status) {
        if (status == MovieStatus.PUBLISHED && movie.getPublishedAt() == null) movie.setPublishedAt(Instant.now());
        movie.setArchivedAt(status == MovieStatus.ARCHIVED ? Instant.now() : null);
    }

    private static String normalizeImageUrl(String value, String label) {
        String normalized = blankToNull(value);
        if (normalized == null) return null;
        if (normalized.startsWith("/") || normalized.startsWith("https://")) return normalized;
        throw new IllegalArgumentException(label + " must use HTTPS or a local /assets path.");
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
