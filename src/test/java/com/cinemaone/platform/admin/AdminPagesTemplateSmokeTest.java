package com.cinemaone.platform.admin;

import com.cinemaone.platform.genre.Genre;
import com.cinemaone.platform.genre.GenreService;
import com.cinemaone.platform.genre.GenreTranslation;
import com.cinemaone.platform.movie.Movie;
import com.cinemaone.platform.movie.MovieService;
import com.cinemaone.platform.movie.MovieStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AdminAuthController.class, AdminDashboardController.class, AdminMovieController.class, AdminGenreController.class})
@AutoConfigureMockMvc(addFilters = false)
class AdminPagesTemplateSmokeTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean MovieService movieService;
    @MockitoBean GenreService genreService;

    private Movie movie;
    private Genre genre;

    @BeforeEach
    void setUp() {
        movie = mock(Movie.class);
        when(movie.getId()).thenReturn(42L);
        when(movie.getPublicId()).thenReturn(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        when(movie.getOriginalTitle()).thenReturn("Neon Horizon");
        when(movie.getOriginalLanguage()).thenReturn("en");
        when(movie.getReleaseDate()).thenReturn(LocalDate.of(2026, 10, 1));
        when(movie.getStatus()).thenReturn(MovieStatus.PUBLISHED);
        when(movie.getUpdatedAt()).thenReturn(Instant.parse("2026-10-03T08:00:00Z"));
        when(movie.getGenres()).thenReturn(new LinkedHashSet<>());
        when(movie.getTranslations()).thenReturn(new LinkedHashSet<>());

        genre = mock(Genre.class);
        when(genre.getId()).thenReturn(7L);
        when(genre.getSlug()).thenReturn("sci-fi");
        when(genre.getEnglishName()).thenReturn("Sci-Fi");
        GenreTranslation translation = mock(GenreTranslation.class);
        when(translation.getLocale()).thenReturn("en");
        when(translation.getName()).thenReturn("Sci-Fi");
        when(genre.getTranslations()).thenReturn(Set.of(translation));

        when(movieService.countAll()).thenReturn(1L);
        when(movieService.countPublished()).thenReturn(1L);
        when(movieService.countDrafts()).thenReturn(0L);
        when(movieService.countArchived()).thenReturn(0L);
        when(movieService.search(anyString(), anyInt(), anyInt()))
            .thenReturn(new PageImpl<>(List.of(movie), PageRequest.of(0, 20), 1));
        when(movieService.get(42L)).thenReturn(movie);
        when(genreService.findAll()).thenReturn(List.of(genre));
    }

    @Test
    void rendersEveryAdminHtmlPage() throws Exception {
        mockMvc.perform(get("/admin/login")).andExpect(status().isOk()).andExpect(content().string(containsString("Welcome back")));
        mockMvc.perform(get("/admin")).andExpect(status().isOk()).andExpect(content().string(containsString("Neon Horizon")));
        mockMvc.perform(get("/admin/movies")).andExpect(status().isOk()).andExpect(content().string(containsString("Neon Horizon")));
        mockMvc.perform(get("/admin/movies/new")).andExpect(status().isOk()).andExpect(content().string(containsString("Add movie")));
        mockMvc.perform(get("/admin/movies/42/edit")).andExpect(status().isOk()).andExpect(content().string(containsString("Edit movie")));
        mockMvc.perform(get("/admin/genres")).andExpect(status().isOk()).andExpect(content().string(containsString("Sci-Fi")));
    }
}
