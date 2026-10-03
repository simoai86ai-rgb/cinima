INSERT INTO genres (slug) VALUES
('action'), ('animation'), ('comedy'), ('drama'), ('horror'), ('mystery'), ('sci-fi'), ('thriller');

INSERT INTO genre_translations (genre_id, locale, name)
SELECT id, 'en', CASE slug
    WHEN 'action' THEN 'Action' WHEN 'animation' THEN 'Animation' WHEN 'comedy' THEN 'Comedy'
    WHEN 'drama' THEN 'Drama' WHEN 'horror' THEN 'Horror' WHEN 'mystery' THEN 'Mystery'
    WHEN 'sci-fi' THEN 'Sci-Fi' WHEN 'thriller' THEN 'Thriller' END
FROM genres;

INSERT INTO genre_translations (genre_id, locale, name)
SELECT id, 'fr', CASE slug
    WHEN 'action' THEN 'Action' WHEN 'animation' THEN 'Animation' WHEN 'comedy' THEN 'Comédie'
    WHEN 'drama' THEN 'Drame' WHEN 'horror' THEN 'Horreur' WHEN 'mystery' THEN 'Mystère'
    WHEN 'sci-fi' THEN 'Science-fiction' WHEN 'thriller' THEN 'Thriller' END
FROM genres;

INSERT INTO genre_translations (genre_id, locale, name)
SELECT id, 'ar', CASE slug
    WHEN 'action' THEN 'أكشن' WHEN 'animation' THEN 'رسوم متحركة' WHEN 'comedy' THEN 'كوميديا'
    WHEN 'drama' THEN 'دراما' WHEN 'horror' THEN 'رعب' WHEN 'mystery' THEN 'غموض'
    WHEN 'sci-fi' THEN 'خيال علمي' WHEN 'thriller' THEN 'إثارة' END
FROM genres;
