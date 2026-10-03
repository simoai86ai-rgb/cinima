package com.cinemaone.platform.publicsite;

import java.util.UUID;

public record MovieCardView(
    UUID publicId,
    String slug,
    String title,
    String shortDescription,
    String posterUrl,
    String backdropUrl,
    Integer year,
    String runtimeLabel,
    String ratingLabel,
    String primaryGenre,
    String detailUrl,
    String watchUrl
) {}
