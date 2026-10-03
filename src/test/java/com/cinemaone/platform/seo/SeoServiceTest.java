package com.cinemaone.platform.seo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeoServiceTest {
    @Test
    void buildsLocalizedCanonicalAndHreflang() {
        SeoService service = new SeoService("https://cinema.example/");
        SeoPage page = service.page("ar", "/movies", "الأفلام", "وصف", "/assets/icons/icon-512.png");

        assertEquals("https://cinema.example/ar/movies", page.canonical());
        assertEquals("https://cinema.example/en/movies", page.alternates().get("en"));
        assertEquals("https://cinema.example/ar/movies", page.alternates().get("ar"));
        assertEquals("https://cinema.example/en/movies", page.alternates().get("x-default"));
        assertTrue(page.jsonLd().contains("WebPage"));
    }
}
