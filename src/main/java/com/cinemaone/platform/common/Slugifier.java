package com.cinemaone.platform.common;

import java.text.Normalizer;
import java.util.Locale;

public final class Slugifier {
    private Slugifier() {}

    public static String slugify(String value) {
        if (value == null || value.isBlank()) return "item";
        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFKC)
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^\\p{L}\\p{Nd}]+", "-")
            .replaceAll("^-+|-+$", "")
            .replaceAll("-{2,}", "-");
        return normalized.isBlank() ? "item" : normalized;
    }
}
