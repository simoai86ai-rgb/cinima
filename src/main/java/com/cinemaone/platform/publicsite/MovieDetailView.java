package com.cinemaone.platform.publicsite;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record MovieDetailView(
    UUID publicId,
    String slug,
    String title,
    String description,
    String shortDescription,
    String posterUrl,
    String backdropUrl,
    Integer year,
    String releaseDate,
    String runtimeLabel,
    String ratingLabel,
    String originalLanguage,
    List<GenreView> genres,
    String detailUrl,
    String watchUrl,
    Map<String, String> localizedDetailUrls,
    String seoTitle,
    String seoDescription
) {}
