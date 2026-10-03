package com.cinemaone.platform.publicsite;

import com.cinemaone.platform.seo.SeoService;
import com.cinemaone.platform.web.SiteLocale;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice(assignableTypes = PublicSiteController.class)
public class PublicNotFoundAdvice {
    private final SeoService seo;

    public PublicNotFoundAdvice(SeoService seo) { this.seo = seo; }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(EntityNotFoundException ex, HttpServletRequest request, Model model) {
        String locale = resolveLocale(request);
        SiteLocale siteLocale = SiteLocale.from(locale);
        Map<String, String> languageLinks = new LinkedHashMap<>();
        for (SiteLocale item : SiteLocale.all()) languageLinks.put(item.code(), "/" + item.code());
        model.addAttribute("locale", locale);
        model.addAttribute("siteLocale", siteLocale);
        model.addAttribute("siteLocales", SiteLocale.all());
        model.addAttribute("languageLinks", languageLinks);
        model.addAttribute("rtl", siteLocale.rtl());
        model.addAttribute("activeNav", "");
        model.addAttribute("seo", seo.noIndex("Not found - Cinema One", "The requested page could not be found.", "/404", "/assets/icons/icon-512.png"));
        return "public/404";
    }

    private static String resolveLocale(HttpServletRequest request) {
        String[] parts = request.getRequestURI().split("/");
        if (parts.length > 1 && SiteLocale.supports(parts[1])) return parts[1];
        String lang = request.getParameter("lang");
        return SiteLocale.supports(lang) ? lang : "en";
    }
}
