package com.cinemaone.platform.seo;

import com.cinemaone.platform.genre.Genre;
import com.cinemaone.platform.genre.GenreRepository;
import com.cinemaone.platform.publicsite.PublicCatalogService;
import com.cinemaone.platform.publicsite.SitemapMovieView;
import com.cinemaone.platform.web.SiteLocale;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SitemapController {
    private static final int MOVIES_PER_SITEMAP = 15000; // max 45k localized URLs when each movie has 3 locales

    private final PublicCatalogService catalog;
    private final GenreRepository genreRepository;
    private final SeoService seo;

    public SitemapController(PublicCatalogService catalog, GenreRepository genreRepository, SeoService seo) {
        this.catalog = catalog;
        this.genreRepository = genreRepository;
        this.seo = seo;
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String sitemapIndex() {
        long movieCount = catalog.publishedCount();
        int movieSitemaps = (int) Math.ceil(movieCount / (double) MOVIES_PER_SITEMAP);
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            .append("<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n")
            .append("  <sitemap><loc>").append(xml(seo.absolute("/sitemap-pages.xml"))).append("</loc></sitemap>\n");
        for (int page = 1; page <= movieSitemaps; page++) {
            xml.append("  <sitemap><loc>").append(xml(seo.absolute("/sitemap-movies-" + page + ".xml"))).append("</loc></sitemap>\n");
        }
        return xml.append("</sitemapindex>\n").toString();
    }

    @GetMapping(value = "/sitemap-pages.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String pageSitemap() {
        List<String> urls = new ArrayList<>();
        List<Genre> genres = genreRepository.findAll();
        for (SiteLocale locale : SiteLocale.all()) {
            String prefix = "/" + locale.code();
            for (String suffix : List.of("", "/movies", "/genres", "/about", "/contact", "/privacy", "/terms")) {
                urls.add(seo.absolute(prefix + suffix));
            }
            for (Genre genre : genres) urls.add(seo.absolute(prefix + "/genre/" + genre.getSlug()));
        }
        StringBuilder xml = urlsetStart();
        urls.forEach(url -> xml.append("  <url><loc>").append(xml(url)).append("</loc></url>\n"));
        return xml.append("</urlset>\n").toString();
    }

    @GetMapping(value = "/sitemap-movies-{page}.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<String> movieSitemap(@PathVariable int page) {
        if (page < 1) return ResponseEntity.notFound().build();
        List<SitemapMovieView> movies = catalog.sitemapMovies(page - 1, MOVIES_PER_SITEMAP);
        if (movies.isEmpty()) return ResponseEntity.notFound().build();

        StringBuilder xml = urlsetStart();
        for (SitemapMovieView movie : movies) {
            String lastMod = movie.updatedAt() == null ? null : DateTimeFormatter.ISO_LOCAL_DATE.format(movie.updatedAt().atZone(ZoneOffset.UTC));
            for (String path : movie.localizedPaths().values()) {
                xml.append("  <url><loc>").append(xml(seo.absolute(path))).append("</loc>");
                if (lastMod != null) xml.append("<lastmod>").append(lastMod).append("</lastmod>");
                xml.append("</url>\n");
            }
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml.append("</urlset>\n").toString());
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> robots() {
        String body = "User-agent: *\nAllow: /\nDisallow: /admin\nDisallow: /admin/\nSitemap: " + seo.absolute("/sitemap.xml") + "\n";
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(body);
    }

    private static StringBuilder urlsetStart() {
        return new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            .append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
    }

    private static String xml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }
}
