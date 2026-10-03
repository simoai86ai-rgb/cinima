package com.cinemaone.platform.seo;

import com.cinemaone.platform.genre.GenreRepository;
import com.cinemaone.platform.publicsite.PublicCatalogService;
import com.cinemaone.platform.publicsite.SitemapMovieView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SitemapController.class)
@AutoConfigureMockMvc(addFilters = false)
class SitemapControllerSmokeTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean PublicCatalogService catalog;
    @MockitoBean GenreRepository genreRepository;
    @MockitoBean SeoService seo;

    @BeforeEach
    void setUp() {
        when(catalog.publishedCount()).thenReturn(1L);
        when(catalog.sitemapMovies(anyInt(), anyInt())).thenReturn(List.of(
            new SitemapMovieView(Instant.parse("2026-10-03T08:00:00Z"), Map.of("en", "/en/movie/neon-horizon"))
        ));
        when(genreRepository.findAll()).thenReturn(List.of());
        when(seo.absolute(anyString())).thenAnswer(invocation -> "https://cinema.example" + invocation.getArgument(0, String.class));
    }

    @Test
    void rendersSitemapAndRobotsEndpoints() throws Exception {
        mockMvc.perform(get("/sitemap.xml")).andExpect(status().isOk()).andExpect(content().string(containsString("sitemap-pages.xml")));
        mockMvc.perform(get("/sitemap-pages.xml")).andExpect(status().isOk()).andExpect(content().string(containsString("https://cinema.example/en")));
        mockMvc.perform(get("/sitemap-movies-1.xml")).andExpect(status().isOk()).andExpect(content().string(containsString("/en/movie/neon-horizon")));
        mockMvc.perform(get("/robots.txt")).andExpect(status().isOk()).andExpect(content().string(containsString("Sitemap: https://cinema.example/sitemap.xml")));
    }
}
