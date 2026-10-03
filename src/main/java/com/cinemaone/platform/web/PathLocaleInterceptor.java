package com.cinemaone.platform.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Locale;

public class PathLocaleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        String[] parts = uri.split("/");
        if (parts.length > 1 && SiteLocale.supports(parts[1])) {
            LocaleContextHolder.setLocale(Locale.forLanguageTag(parts[1]));
        } else if (uri.startsWith("/watch/")) {
            String lang = request.getParameter("lang");
            if (SiteLocale.supports(lang)) LocaleContextHolder.setLocale(Locale.forLanguageTag(lang));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LocaleContextHolder.resetLocaleContext();
    }
}
