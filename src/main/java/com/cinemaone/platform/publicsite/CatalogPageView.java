package com.cinemaone.platform.publicsite;

import java.util.List;

public record CatalogPageView(
    List<MovieCardView> movies,
    int page,
    int totalPages,
    long totalElements,
    boolean hasPrevious,
    boolean hasNext
) {}
