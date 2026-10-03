package com.cinemaone.platform.genre;

import com.cinemaone.platform.common.Slugifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class GenreService {
    private final GenreRepository repository;

    public GenreService(GenreRepository repository) { this.repository = repository; }

    public List<Genre> findAll() {
        return repository.findAll().stream().sorted(Comparator.comparing(Genre::getSlug)).toList();
    }

    @Transactional
    public Genre create(GenreForm form) {
        String slug = Slugifier.slugify(form.getSlug());
        if (repository.existsBySlug(slug)) throw new IllegalArgumentException("Genre slug already exists.");
        Genre genre = new Genre();
        genre.setSlug(slug);
        genre.addTranslation(translation("en", form.getNameEn()));
        genre.addTranslation(translation("fr", form.getNameFr()));
        genre.addTranslation(translation("ar", form.getNameAr()));
        return repository.save(genre);
    }

    @Transactional
    public void delete(Long id) {
        try {
            repository.deleteById(id);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("This genre is assigned to one or more movies and cannot be deleted yet.");
        }
    }

    private GenreTranslation translation(String locale, String name) {
        GenreTranslation translation = new GenreTranslation();
        translation.setLocale(locale);
        translation.setName(name.trim());
        return translation;
    }
}
