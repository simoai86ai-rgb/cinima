package com.cinemaone.platform;

import com.cinemaone.platform.genre.Genre;
import com.cinemaone.platform.genre.GenreForm;
import com.cinemaone.platform.genre.GenreRepository;
import com.cinemaone.platform.genre.GenreService;
import com.cinemaone.platform.movie.Movie;
import com.cinemaone.platform.movie.MovieForm;
import com.cinemaone.platform.movie.MovieRepository;
import com.cinemaone.platform.movie.MovieService;
import com.cinemaone.platform.movie.MovieStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:routes;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "app.demo-content.enabled=false",
    "app.bootstrap-admin.enabled=false",
    "app.site-base-url=http://localhost:8080"
})
@AutoConfigureMockMvc
class FullApplicationRouteSmokeTest {

    @Autowired MockMvc mockMvc;
    @Autowired GenreService genreService;
    @Autowired GenreRepository genreRepository;
    @Autowired MovieService movieService;
    @Autowired MovieRepository movieRepository;

    private Movie movie;

    @BeforeEach
    void seedCatalog() {
        if (genreRepository.findBySlug("sci-fi").isEmpty()) {
            GenreForm genre = new GenreForm();
            genre.setSlug("sci-fi");
            genre.setNameEn("Sci-Fi");
            genre.setNameFr("Science-fiction");
            genre.setNameAr("خيال علمي");
            genreService.create(genre);
        }

        if (movieRepository.count() == 0) {
            Genre genre = genreRepository.findBySlug("sci-fi").orElseThrow();
            MovieForm form = new MovieForm();
            form.setOriginalTitle("Neon Horizon");
            form.setOriginalLanguage("en");
            form.setReleaseDate(LocalDate.of(2026, 10, 1));
            form.setRuntimeMinutes(138);
            form.setVoteAverage(new BigDecimal("8.7"));
            form.setVoteCount(1000);
            form.setStatus(MovieStatus.PUBLISHED);
            form.setGenreIds(java.util.List.of(genre.getId()));
            form.setPosterUrl("/assets/img/posters/neon-horizon.png");
            form.setBackdropUrl("/assets/img/backdrops/neon-horizon.svg");
            for (MovieForm.TranslationInput input : form.getTranslations()) {
                input.setSlug("neon-horizon");
                if ("en".equals(input.getLocale())) {
                    input.setTitle("Neon Horizon");
                    input.setDescription("A cinematic integration test movie.");
                } else if ("fr".equals(input.getLocale())) {
                    input.setTitle("Horizon néon");
                    input.setDescription("Un film de test d'intégration cinématographique.");
                } else {
                    input.setTitle("أفق النيون");
                    input.setDescription("فيلم تجريبي لاختبار التكامل.");
                }
                input.setShortDescription(input.getDescription());
                input.setSeoTitle(input.getTitle() + " - Cinema One");
                input.setSeoDescription(input.getDescription());
            }
            movie = movieService.create(form);
        } else {
            movie = movieRepository.findAll().getFirst();
        }
    }

    @Test
    void everyPublicRouteRendersWithRealServicesAndDatabase() throws Exception {
        for (String locale : java.util.List.of("en", "fr", "ar")) {
            mockMvc.perform(get("/" + locale)).andExpect(status().isOk());
            mockMvc.perform(get("/" + locale + "/movies")).andExpect(status().isOk());
            mockMvc.perform(get("/" + locale + "/search").param("q", "Neon")).andExpect(status().isOk());
            mockMvc.perform(get("/" + locale + "/genres")).andExpect(status().isOk());
            mockMvc.perform(get("/" + locale + "/genre/sci-fi")).andExpect(status().isOk());
            mockMvc.perform(get("/" + locale + "/movie/neon-horizon")).andExpect(status().isOk());
            mockMvc.perform(get("/" + locale + "/about")).andExpect(status().isOk());
            mockMvc.perform(get("/" + locale + "/contact")).andExpect(status().isOk());
            mockMvc.perform(get("/" + locale + "/privacy")).andExpect(status().isOk());
            mockMvc.perform(get("/" + locale + "/terms")).andExpect(status().isOk());
        }

        mockMvc.perform(get("/watch/" + movie.getPublicId()).param("lang", "en"))
            .andExpect(status().isOk());
        mockMvc.perform(get("/sitemap.xml")).andExpect(status().isOk()).andExpect(content().string(containsString("sitemap-pages.xml")));
        mockMvc.perform(get("/sitemap-pages.xml")).andExpect(status().isOk());
        mockMvc.perform(get("/sitemap-movies-1.xml")).andExpect(status().isOk());
        mockMvc.perform(get("/robots.txt")).andExpect(status().isOk());
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void redirectsAndNotFoundRoutesBehaveCorrectly() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/en"));
        mockMvc.perform(get("/movies")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/en/movies"));
        mockMvc.perform(get("/en/movie/does-not-exist")).andExpect(status().isNotFound());
    }
}
