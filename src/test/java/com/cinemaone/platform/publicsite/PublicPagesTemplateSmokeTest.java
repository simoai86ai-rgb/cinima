package com.cinemaone.platform.publicsite;

import com.cinemaone.platform.seo.SeoPage;
import com.cinemaone.platform.seo.SeoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicSiteController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicPagesTemplateSmokeTest {

    private static final UUID MOVIE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired MockMvc mockMvc;
    @MockitoBean PublicCatalogService catalog;
    @MockitoBean SeoService seo;

    private MovieCardView card;
    private MovieDetailView detail;
    private GenreView genre;
    private SeoPage seoPage;

    @BeforeEach
    void setUp() {
        genre = new GenreView("sci-fi", "Sci-Fi", "/en/genre/sci-fi");
        card = new MovieCardView(
            MOVIE_ID, "neon-horizon", "Neon Horizon", "A cinematic test movie.",
            "/assets/img/posters/neon-horizon.png", "/assets/img/backdrops/neon-horizon.svg",
            2026, "2h 18m", "8.7", "Sci-Fi", "/en/movie/neon-horizon",
            "/watch/" + MOVIE_ID + "?lang=en"
        );
        detail = new MovieDetailView(
            MOVIE_ID, "neon-horizon", "Neon Horizon", "A complete storyline.", "A cinematic test movie.",
            "/assets/img/posters/neon-horizon.png", "/assets/img/backdrops/neon-horizon.svg",
            2026, "2026-10-01", "2h 18m", "8.7", "en", List.of(genre),
            "/en/movie/neon-horizon", "/watch/" + MOVIE_ID + "?lang=en",
            Map.of(
                "en", "/en/movie/neon-horizon",
                "fr", "/fr/movie/neon-horizon",
                "ar", "/ar/movie/neon-horizon"
            ),
            "Neon Horizon - Cinema One", "A cinematic test movie."
        );
        seoPage = new SeoPage(
            "Cinema One", "Cinema One catalog", "http://localhost:8080/en",
            "http://localhost:8080/assets/img/social/neon-horizon.png", "index,follow",
            Map.of("en", "http://localhost:8080/en"), ""
        );

        when(catalog.latest(anyString(), anyInt())).thenReturn(List.of(card));
        when(catalog.trending(anyString(), anyInt())).thenReturn(List.of(card));
        when(catalog.genres(anyString())).thenReturn(List.of(genre));
        when(catalog.availableYears()).thenReturn(List.of(2026));
        when(catalog.search(anyString(), anyString(), anyString(), nullable(Integer.class), anyInt(), anyInt()))
            .thenReturn(new CatalogPageView(List.of(card), 0, 1, 1, false, false));
        when(catalog.genre(anyString(), anyString())).thenReturn(genre);
        when(catalog.resolveMovieRoute(anyString(), anyString())).thenReturn(new MovieRouteResolution(detail, null));
        when(catalog.detailByPublicId(anyString(), any(UUID.class))).thenReturn(detail);
        when(catalog.related(anyString(), any(MovieDetailView.class), anyInt())).thenReturn(List.of(card));

        when(seo.home(anyString(), anyString(), anyString(), anyString())).thenReturn(seoPage);
        when(seo.page(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(seoPage);
        when(seo.listing(anyString(), anyString(), anyString(), anyString(), anyList(), anyString())).thenReturn(seoPage);
        when(seo.noIndex(anyString(), anyString(), anyString(), anyString())).thenReturn(seoPage);
        when(seo.movie(anyString(), any(MovieDetailView.class))).thenReturn(seoPage);
    }

    @Test
    void rendersEveryPublicHtmlPage() throws Exception {
        for (String locale : List.of("en", "fr", "ar")) {
            mockMvc.perform(get("/" + locale)).andExpect(status().isOk()).andExpect(content().string(containsString("Neon Horizon")));
            mockMvc.perform(get("/" + locale + "/movies")).andExpect(status().isOk()).andExpect(content().string(containsString("Neon Horizon")));
            mockMvc.perform(get("/" + locale + "/search").param("q", "Neon")).andExpect(status().isOk()).andExpect(content().string(containsString("Neon Horizon")));
            mockMvc.perform(get("/" + locale + "/genres")).andExpect(status().isOk()).andExpect(content().string(containsString("Sci-Fi")));
            mockMvc.perform(get("/" + locale + "/genre/sci-fi")).andExpect(status().isOk()).andExpect(content().string(containsString("Neon Horizon")));
            mockMvc.perform(get("/" + locale + "/movie/neon-horizon")).andExpect(status().isOk()).andExpect(content().string(containsString("Neon Horizon")));
            mockMvc.perform(get("/" + locale + "/about")).andExpect(status().isOk());
            mockMvc.perform(get("/" + locale + "/contact")).andExpect(status().isOk());
            mockMvc.perform(get("/" + locale + "/privacy")).andExpect(status().isOk());
            mockMvc.perform(get("/" + locale + "/terms")).andExpect(status().isOk());
        }
        mockMvc.perform(get("/watch/" + MOVIE_ID).param("lang", "en"))
            .andExpect(status().isOk()).andExpect(content().string(containsString("Neon Horizon")));
    }

    @Test
    void homeRendersRankedCardInsideIterationWithoutLosingLoopVariables() throws Exception {
        mockMvc.perform(get("/en"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("01 / 01")))
            .andExpect(content().string(containsString("data-rank=\"1\"")));
    }

    @Test
    void rendersLocalizedNotFoundPage() throws Exception {
        when(catalog.resolveMovieRoute(eq("en"), eq("missing"))).thenThrow(new EntityNotFoundException("Movie not found"));
        mockMvc.perform(get("/en/movie/missing"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("404")));
    }

    @Test
    void redirectsRootAndLegacyRoutes() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/en"));
        mockMvc.perform(get("/movies")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/en/movies"));
        mockMvc.perform(get("/genres")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/en/genres"));
    }
}
