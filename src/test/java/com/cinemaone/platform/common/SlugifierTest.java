package com.cinemaone.platform.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlugifierTest {
    @Test
    void slugifiesLatinAndArabicText() {
        assertEquals("neon-horizon", Slugifier.slugify("Neon Horizon"));
        assertEquals("ملك-الصحراء", Slugifier.slugify("ملك الصحراء"));
    }
}
