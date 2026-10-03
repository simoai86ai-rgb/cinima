package com.cinemaone.platform.publicsite;

import java.time.Instant;
import java.util.Map;

public record SitemapMovieView(Instant updatedAt, Map<String, String> localizedPaths) {}
