package com.cinemaone.platform.web;

import java.util.Arrays;
import java.util.List;

public enum SiteLocale {
    EN("en", "English", "EN", false),
    FR("fr", "Français", "FR", false),
    AR("ar", "العربية", "AR", true);

    private final String code;
    private final String nativeName;
    private final String shortLabel;
    private final boolean rtl;

    SiteLocale(String code, String nativeName, String shortLabel, boolean rtl) {
        this.code = code;
        this.nativeName = nativeName;
        this.shortLabel = shortLabel;
        this.rtl = rtl;
    }

    public String code() { return code; }
    public String nativeName() { return nativeName; }
    public String shortLabel() { return shortLabel; }
    public boolean rtl() { return rtl; }
    public String getCode() { return code; }
    public String getNativeName() { return nativeName; }
    public String getShortLabel() { return shortLabel; }
    public boolean isRtl() { return rtl; }

    public static SiteLocale from(String value) {
        return Arrays.stream(values())
            .filter(locale -> locale.code.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported locale: " + value));
    }

    public static boolean supports(String value) {
        return Arrays.stream(values()).anyMatch(locale -> locale.code.equalsIgnoreCase(value));
    }

    public static List<SiteLocale> all() { return List.of(values()); }
}
