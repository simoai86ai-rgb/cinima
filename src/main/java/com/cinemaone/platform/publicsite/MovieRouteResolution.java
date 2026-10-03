package com.cinemaone.platform.publicsite;

public record MovieRouteResolution(MovieDetailView movie, String permanentRedirectUrl) {
    public boolean redirect() { return permanentRedirectUrl != null && !permanentRedirectUrl.isBlank(); }
}
