package com.cinemaone.platform.publicsite;

import com.cinemaone.platform.seo.SeoPage;
import com.cinemaone.platform.seo.SeoService;
import com.cinemaone.platform.web.SiteLocale;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Controller
public class PublicSiteController {
    private static final String SOCIAL_IMAGE = "/assets/img/social/neon-horizon.png";

    private final PublicCatalogService catalog;
    private final SeoService seo;
    private final MessageSource messages;

    public PublicSiteController(PublicCatalogService catalog, SeoService seo, MessageSource messages) {
        this.catalog = catalog;
        this.seo = seo;
        this.messages = messages;
    }

    @GetMapping("/")
    public String root() { return "redirect:/en"; }


    @GetMapping({"/movies", "/genres", "/about", "/contact", "/privacy", "/terms"})
    public String legacyPublicRedirect(jakarta.servlet.http.HttpServletRequest request) {
        return "redirect:/en" + request.getRequestURI();
    }

    @GetMapping("/{locale:en|fr|ar}")
    public String home(@PathVariable String locale, Model model) {
        common(model, locale, "home");
        setLanguageLinks(model, "");
        List<MovieCardView> latest = catalog.latest(locale, 12);
        List<MovieCardView> trending = catalog.trending(locale, 10);
        List<MovieCardView> hero = latest.stream().limit(5).toList();
        List<MovieCardView> recommended = trending.size() > 1 ? trending.reversed().stream().limit(10).toList() : trending;
        model.addAttribute("heroMovies", hero);
        model.addAttribute("trendingMovies", trending);
        model.addAttribute("latestMovies", latest);
        model.addAttribute("recommendedMovies", recommended);
        model.addAttribute("genres", catalog.genres(locale));
        model.addAttribute("seo", seo.home(locale, msg("seo.home.title", locale), msg("seo.home.description", locale),
            hero.isEmpty() ? SOCIAL_IMAGE : hero.getFirst().backdropUrl()));
        return "public/home";
    }

    @GetMapping("/{locale:en|fr|ar}/movies")
    public String movies(@PathVariable String locale,
                         @RequestParam(defaultValue = "") String q,
                         @RequestParam(defaultValue = "") String genre,
                         @RequestParam(required = false) Integer year,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {
        common(model, locale, "movies");
        setLanguageLinks(model, "/movies");
        CatalogPageView catalogPage = catalog.search(locale, q, genre, year, page, 18);
        model.addAttribute("catalogPage", catalogPage);
        model.addAttribute("genres", catalog.genres(locale));
        model.addAttribute("years", catalog.availableYears());
        model.addAttribute("q", q);
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedYear", year);
        SeoPage pageSeo = (q.isBlank() && genre.isBlank() && year == null)
            ? seo.listing(locale, "/movies", msg("seo.movies.title", locale), msg("seo.movies.description", locale), catalogPage.movies(), SOCIAL_IMAGE)
            : seo.noIndex(msg("seo.movies.title", locale), msg("seo.movies.description", locale), "/" + locale + "/movies", SOCIAL_IMAGE);
        model.addAttribute("seo", pageSeo);
        return "public/movies";
    }

    @GetMapping("/{locale:en|fr|ar}/search")
    public String search(@PathVariable String locale,
                         @RequestParam(defaultValue = "") String q,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {
        common(model, locale, "search");
        setLanguageLinks(model, "/search");
        CatalogPageView catalogPage = catalog.search(locale, q, "", null, page, 18);
        model.addAttribute("catalogPage", catalogPage);
        model.addAttribute("q", q);
        model.addAttribute("seo", seo.noIndex(msg("seo.search.title", locale), msg("seo.search.description", locale),
            "/" + locale + "/search", SOCIAL_IMAGE));
        return "public/search";
    }

    @GetMapping("/{locale:en|fr|ar}/genre/{slug}")
    public String genre(@PathVariable String locale, @PathVariable String slug,
                        @RequestParam(defaultValue = "0") int page, Model model) {
        common(model, locale, "genres");
        setLanguageLinks(model, "/genre/" + slug);
        GenreView genre = catalog.genre(locale, slug);
        CatalogPageView catalogPage = catalog.search(locale, "", slug, null, page, 18);
        model.addAttribute("genre", genre);
        model.addAttribute("catalogPage", catalogPage);
        String title = genre.name() + " - Cinema One";
        String description = msg("seo.genre.description", locale).replace("{genre}", genre.name());
        model.addAttribute("seo", seo.listing(locale, "/genre/" + slug, title, description, catalogPage.movies(), SOCIAL_IMAGE));
        return "public/genre";
    }

    @GetMapping("/{locale:en|fr|ar}/genres")
    public String genres(@PathVariable String locale, Model model) {
        common(model, locale, "genres");
        setLanguageLinks(model, "/genres");
        model.addAttribute("genres", catalog.genres(locale));
        model.addAttribute("seo", seo.page(locale, "/genres", msg("seo.genres.title", locale), msg("seo.genres.description", locale), SOCIAL_IMAGE));
        return "public/genres";
    }

    @GetMapping("/{locale:en|fr|ar}/movie/{slug}")
    public ModelAndView movie(@PathVariable String locale, @PathVariable String slug, Model model) {
        MovieRouteResolution resolution = catalog.resolveMovieRoute(locale, slug);
        if (resolution.redirect()) {
            RedirectView redirect = new RedirectView(resolution.permanentRedirectUrl(), true);
            redirect.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            return new ModelAndView(redirect);
        }

        common(model, locale, "movies");
        MovieDetailView movie = resolution.movie();
        model.addAttribute("movie", movie);
        model.addAttribute("languageLinks", movie.localizedDetailUrls());
        model.addAttribute("relatedMovies", catalog.related(locale, movie, 10));
        model.addAttribute("seo", seo.movie(locale, movie));
        ModelAndView view = new ModelAndView("public/movie");
        view.addAllObjects(model.asMap());
        return view;
    }

    @GetMapping("/watch/{publicId}")
    public String watch(@PathVariable UUID publicId, @RequestParam(defaultValue = "en") String lang, Model model) {
        String locale = SiteLocale.supports(lang) ? lang : "en";
        common(model, locale, "watch");
        MovieDetailView movie = catalog.detailByPublicId(locale, publicId);
        model.addAttribute("movie", movie);
        java.util.Map<String, String> watchLinks = new java.util.LinkedHashMap<>();
        for (SiteLocale item : SiteLocale.all()) watchLinks.put(item.code(), "/watch/" + publicId + "?lang=" + item.code());
        model.addAttribute("languageLinks", watchLinks);
        model.addAttribute("seo", seo.noIndex(movie.title() + " - Watch - Cinema One", movie.shortDescription(),
            "/watch/" + publicId, movie.backdropUrl()));
        return "public/watch";
    }

    @GetMapping("/{locale:en|fr|ar}/about")
    public String about(@PathVariable String locale, Model model) { return infoPage(model, locale, "about", "seo.about.title", "seo.about.description"); }

    @GetMapping("/{locale:en|fr|ar}/contact")
    public String contact(@PathVariable String locale, Model model) { return infoPage(model, locale, "contact", "seo.contact.title", "seo.contact.description"); }

    @GetMapping("/{locale:en|fr|ar}/privacy")
    public String privacy(@PathVariable String locale, Model model) { return infoPage(model, locale, "privacy", "seo.privacy.title", "seo.privacy.description"); }

    @GetMapping("/{locale:en|fr|ar}/terms")
    public String terms(@PathVariable String locale, Model model) { return infoPage(model, locale, "terms", "seo.terms.title", "seo.terms.description"); }

    private String infoPage(Model model, String locale, String page, String titleKey, String descriptionKey) {
        common(model, locale, page);
        setLanguageLinks(model, "/" + page);
        model.addAttribute("seo", seo.page(locale, "/" + page, msg(titleKey, locale), msg(descriptionKey, locale), SOCIAL_IMAGE));
        return "public/" + page;
    }

    private void common(Model model, String locale, String activeNav) {
        SiteLocale siteLocale = SiteLocale.from(locale);
        model.addAttribute("locale", locale);
        model.addAttribute("siteLocale", siteLocale);
        model.addAttribute("siteLocales", SiteLocale.all());
        model.addAttribute("rtl", siteLocale.rtl());
        model.addAttribute("activeNav", activeNav);
    }

    private void setLanguageLinks(Model model, String suffix) {
        java.util.Map<String, String> links = new java.util.LinkedHashMap<>();
        for (SiteLocale item : SiteLocale.all()) links.put(item.code(), "/" + item.code() + suffix);
        model.addAttribute("languageLinks", links);
    }

    private String msg(String key, String locale) {
        return messages.getMessage(key, null, Locale.forLanguageTag(locale));
    }
}
