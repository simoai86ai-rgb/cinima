package com.cinemaone.platform.genre;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GenreForm {
    @NotBlank @Size(max = 120)
    private String slug;
    @NotBlank @Size(max = 160)
    private String nameEn;
    @NotBlank @Size(max = 160)
    private String nameFr;
    @NotBlank @Size(max = 160)
    private String nameAr;

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public String getNameFr() { return nameFr; }
    public void setNameFr(String nameFr) { this.nameFr = nameFr; }
    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }
}
