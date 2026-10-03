package com.cinemaone.platform.seo;

import com.cinemaone.platform.publicsite.MovieCardView;
import com.cinemaone.platform.publicsite.MovieDetailView;
import com.cinemaone.platform.web.SiteLocale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeoService {
    private final String baseUrl;

    public SeoService(@Value("${app.site-base-url:http://localhost:8080}") String baseUrl) {
        this.baseUrl = trimSlash(baseUrl);
    }

    public String baseUrl() { return baseUrl; }

    public SeoPage home(String locale, String title, String description, String image) {
        Map<String, String> alternates = simpleAlternates("");
        String canonical = absolute("/" + locale);
        String json = "{" +
            q("@context") + ":" + q("https://schema.org") + "," +
            q("@graph") + ":[{" +
                q("@type") + ":" + q("Organization") + "," +
                q("@id") + ":" + q(baseUrl + "/#organization") + "," +
                q("name") + ":" + q("Cinema One") + "," +
                q("url") + ":" + q(baseUrl + "/") +
            "},{" +
                q("@type") + ":" + q("WebSite") + "," +
                q("@id") + ":" + q(baseUrl + "/#website") + "," +
                q("url") + ":" + q(baseUrl + "/") + "," +
                q("name") + ":" + q("Cinema One") + "," +
                q("inLanguage") + ":[" + q("en") + "," + q("fr") + "," + q("ar") + "]" +
            "}]}";
        return new SeoPage(title, description, canonical, absoluteAsset(image), "index,follow,max-image-preview:large", alternates, json);
    }

    public SeoPage page(String locale, String pathSuffix, String title, String description, String image) {
        Map<String, String> alternates = simpleAlternates(pathSuffix);
        String canonical = absolute("/" + locale + pathSuffix);
        String json = "{" + q("@context") + ":" + q("https://schema.org") + "," +
            q("@type") + ":" + q("WebPage") + "," + q("name") + ":" + q(title) + "," +
            q("description") + ":" + q(description) + "," + q("url") + ":" + q(canonical) + "}";
        return new SeoPage(title, description, canonical, absoluteAsset(image), "index,follow,max-image-preview:large", alternates, json);
    }

    public SeoPage listing(String locale, String pathSuffix, String title, String description, List<MovieCardView> movies, String image) {
        Map<String, String> alternates = simpleAlternates(pathSuffix);
        String canonical = absolute("/" + locale + pathSuffix);
        StringBuilder items = new StringBuilder();
        for (int i = 0; i < movies.size(); i++) {
            if (i > 0) items.append(',');
            MovieCardView movie = movies.get(i);
            items.append('{')
                .append(q("@type")).append(':').append(q("ListItem")).append(',')
                .append(q("position")).append(':').append(i + 1).append(',')
                .append(q("url")).append(':').append(q(absolute(movie.detailUrl())))
                .append('}');
        }
        String json = "{" + q("@context") + ":" + q("https://schema.org") + "," +
            q("@type") + ":" + q("ItemList") + "," + q("name") + ":" + q(title) + "," +
            q("itemListElement") + ":[" + items + "]}";
        return new SeoPage(title, description, canonical, absoluteAsset(image), "index,follow,max-image-preview:large", alternates, json);
    }

    public SeoPage movie(String locale, MovieDetailView movie) {
        Map<String, String> alternates = new LinkedHashMap<>();
        movie.localizedDetailUrls().forEach((code, path) -> alternates.put(code, absolute(path)));
        alternates.put("x-default", absolute(movie.localizedDetailUrls().getOrDefault("en", movie.detailUrl())));
        String canonical = absolute(movie.detailUrl());
        String genres = movie.genres().stream().map(genre -> q(genre.name())).reduce((a, b) -> a + "," + b).orElse("");
        String json = "{" + q("@context") + ":" + q("https://schema.org") + "," + q("@graph") + ":[{" +
            q("@type") + ":" + q("Movie") + "," +
            q("name") + ":" + q(movie.title()) + "," +
            q("description") + ":" + q(movie.description()) + "," +
            q("url") + ":" + q(canonical) + "," +
            q("image") + ":" + q(absoluteAsset(movie.posterUrl())) +
            (movie.releaseDate().isBlank() ? "" : "," + q("dateCreated") + ":" + q(movie.releaseDate())) +
            (genres.isBlank() ? "" : "," + q("genre") + ":[" + genres + "]") +
            "},{" +
            q("@type") + ":" + q("BreadcrumbList") + "," +
            q("itemListElement") + ":[" +
                breadcrumb(1, "Cinema One", absolute("/" + locale)) + "," +
                breadcrumb(2, "Movies", absolute("/" + locale + "/movies")) + "," +
                breadcrumb(3, movie.title(), canonical) +
            "]}]}";
        return new SeoPage(movie.seoTitle(), movie.seoDescription(), canonical, absoluteAsset(movie.backdropUrl()), "index,follow,max-image-preview:large", alternates, json);
    }

    public SeoPage noIndex(String title, String description, String path, String image) {
        return new SeoPage(title, description, absolute(path), absoluteAsset(image), "noindex,follow", Map.of(), "");
    }

    public String absolute(String path) {
        if (path == null || path.isBlank()) return baseUrl;
        if (path.startsWith("http://") || path.startsWith("https://")) return path;
        String normalized = path.startsWith("/") ? path : "/" + path;
        return UriComponentsBuilder.fromUriString(baseUrl).path(normalized).build().encode().toUriString();
    }

    public String absoluteAsset(String value) {
        return absolute(value == null || value.isBlank() ? "/assets/icons/icon-512.png" : value);
    }

    private Map<String, String> simpleAlternates(String suffix) {
        Map<String, String> map = new LinkedHashMap<>();
        for (SiteLocale locale : SiteLocale.all()) map.put(locale.code(), absolute("/" + locale.code() + suffix));
        map.put("x-default", absolute("/en" + suffix));
        return map;
    }

    private static String breadcrumb(int position, String name, String url) {
        return "{" + q("@type") + ":" + q("ListItem") + "," + q("position") + ":" + position + "," +
            q("name") + ":" + q(name) + "," + q("item") + ":" + q(url) + "}";
    }

    private static String q(String value) {
        if (value == null) return "\"\"";
        StringBuilder out = new StringBuilder(value.length() + 8).append('"');
        for (char c : value.toCharArray()) {
            switch (c) {
                case '\\' -> out.append("\\\\");
                case '"' -> out.append("\\\"");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                case '<' -> out.append("\\u003c");
                case '>' -> out.append("\\u003e");
                case '&' -> out.append("\\u0026");
                default -> out.append(c);
            }
        }
        return out.append('"').toString();
    }

    private static String trimSlash(String value) {
        String normalized = value == null || value.isBlank() ? "http://localhost:8080" : value.trim();
        while (normalized.endsWith("/")) normalized = normalized.substring(0, normalized.length() - 1);
        return normalized;
    }
}
