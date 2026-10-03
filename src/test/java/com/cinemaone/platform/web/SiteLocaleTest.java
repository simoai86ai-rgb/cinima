package com.cinemaone.platform.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SiteLocaleTest {
    @Test
    void supportsConfiguredLocales() {
        assertTrue(SiteLocale.supports("en"));
        assertTrue(SiteLocale.supports("fr"));
        assertTrue(SiteLocale.supports("ar"));
        assertFalse(SiteLocale.supports("es"));
        assertFalse(SiteLocale.supports(null));
        assertTrue(SiteLocale.AR.rtl());
        assertFalse(SiteLocale.EN.rtl());
    }
}
