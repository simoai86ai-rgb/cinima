package com.cinemaone.platform.publicsite;

import com.cinemaone.platform.seo.SeoPage;
import com.cinemaone.platform.seo.SeoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicSiteController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicHomeTemplateTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PublicCatalogService catalog;

    @MockitoBean
    private SeoService seo;

    private MovieCardView movie;

    @BeforeEach
    void setUp() {
        movie = new MovieCardView(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "neon-horizon",
            "Neon Horizon",
            "A cinematic test movie.",
            "/assets/img/posters/neon-horizon.png",
            "/assets/img/backdrops/neon-horizon.svg",
            2026,
            "2h 18m",
            "8.7",
            "Sci-Fi",
            "/en/movie/neon-horizon",
            "/watch/11111111-1111-1111-1111-111111111111?lang=en"
        );

        when(catalog.latest("en", 12)).thenReturn(List.of(movie));
        when(catalog.trending("en", 10)).thenReturn(List.of(movie));
        when(catalog.genres("en")).thenReturn(List.of(new GenreView("sci-fi", "Sci-Fi", "/en/genre/sci-fi")));
        when(seo.home(eq("en"), anyString(), anyString(), anyString())).thenReturn(
            new SeoPage(
                "Cinema One",
                "Cinema One catalog",
                "http://localhost:8080/en",
                "http://localhost:8080/assets/img/social/neon-horizon.png",
                "index,follow",
                Map.of("en", "http://localhost:8080/en"),
                ""
            )
        );
    }

    @Test
    void rendersHomeHeroAndPaddedSlideCounter() throws Exception {
        mockMvc.perform(get("/en"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Neon Horizon")))
            .andExpect(content().string(containsString("01 / 01")));
    }
}
