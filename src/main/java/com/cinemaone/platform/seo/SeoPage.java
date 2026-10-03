package com.cinemaone.platform.seo;

import java.util.Map;

public record SeoPage(
    String title,
    String description,
    String canonical,
    String ogImage,
    String robots,
    Map<String, String> alternates,
    String jsonLd
) {}
