package com.cinemaone.platform.config;

import com.cinemaone.platform.genre.Genre;
import com.cinemaone.platform.genre.GenreRepository;
import com.cinemaone.platform.movie.MovieForm;
import com.cinemaone.platform.movie.MovieRepository;
import com.cinemaone.platform.movie.MovieService;
import com.cinemaone.platform.movie.MovieStatus;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DemoCatalogBootstrapConfig {
    @Bean
    @ConditionalOnProperty(name = "app.demo-content.enabled", havingValue = "true")
    ApplicationRunner demoCatalogBootstrap(MovieRepository movieRepository, GenreRepository genreRepository, MovieService movieService) {
        return args -> {
            if (movieRepository.count() > 0) return;
            create(movieService, genreRepository, "Neon Horizon", "أفق النيون", "Horizon néon",
                "A rescue pilot crosses a forbidden orbital boundary and discovers a signal that could rewrite humanity's future.",
                "طيار إنقاذ يعبر حدودًا مدارية محظورة ويكتشف إشارة قد تغيّر مستقبل البشرية.",
                "Un pilote de sauvetage franchit une frontière orbitale interdite et découvre un signal capable de changer l'avenir de l'humanité.",
                "sci-fi", "neon-horizon", LocalDate.of(2026, 9, 18), 138, "8.7", 18420);
            create(movieService, genreRepository, "Atlas Protocol", "بروتوكول أطلس", "Protocole Atlas",
                "An intelligence analyst uncovers a protocol that can predict geopolitical crises before they happen.",
                "محلل استخبارات يكتشف بروتوكولًا قادرًا على التنبؤ بالأزمات قبل وقوعها.",
                "Un analyste du renseignement découvre un protocole capable d'anticiper les crises avant qu'elles n'éclatent.",
                "thriller", "atlas-protocol", LocalDate.of(2026, 8, 29), 124, "8.3", 13750);
            create(movieService, genreRepository, "Desert Echo", "صدى الصحراء", "Écho du désert",
                "A sound engineer follows a mysterious recording deep into a desert where memory and reality begin to overlap.",
                "مهندس صوت يتتبع تسجيلًا غامضًا إلى عمق الصحراء حيث تتداخل الذاكرة مع الواقع.",
                "Un ingénieur du son suit un enregistrement mystérieux au cœur du désert, où mémoire et réalité se confondent.",
                "mystery", "desert-echo", LocalDate.of(2026, 7, 10), 111, "7.9", 8920);
            create(movieService, genreRepository, "Black Tide", "المد الأسود", "Marée noire",
                "A coastal rescue crew races against a manufactured storm hiding a much larger operation offshore.",
                "فريق إنقاذ ساحلي يسابق عاصفة مصطنعة تخفي عملية أكبر في عرض البحر.",
                "Une équipe de secours côtière affronte une tempête artificielle qui dissimule une opération bien plus vaste au large.",
                "action", "black-tide", LocalDate.of(2026, 6, 21), 119, "8.1", 11640);
            create(movieService, genreRepository, "Paper Moon", "قمر من ورق", "Lune de papier",
                "Two strangers rebuild an abandoned cinema and slowly uncover the story that once connected their families.",
                "غريبان يعيدان ترميم سينما مهجورة ويكتشفان تدريجيًا قصة ربطت عائلتيهما.",
                "Deux inconnus restaurent un cinéma abandonné et découvrent peu à peu l'histoire qui reliait autrefois leurs familles.",
                "drama", "paper-moon", LocalDate.of(2026, 5, 8), 104, "7.8", 6410);
            create(movieService, genreRepository, "Signal Zero", "الإشارة صفر", "Signal zéro",
                "When every satellite goes silent, a systems engineer finds one impossible transmission still moving through the network.",
                "عندما تصمت جميع الأقمار الصناعية، يعثر مهندس أنظمة على إرسال مستحيل ما زال يتحرك عبر الشبكة.",
                "Lorsque tous les satellites deviennent silencieux, un ingénieur système découvre une transmission impossible encore active dans le réseau.",
                "sci-fi", "signal-zero", LocalDate.of(2026, 4, 17), 128, "8.5", 15430);
            create(movieService, genreRepository, "Northbound", "نحو الشمال", "Cap au nord",
                "A winter road trip becomes a quiet test of friendship, distance and the promises people keep.",
                "رحلة شتوية نحو الشمال تتحول إلى اختبار هادئ للصداقة والمسافة والوعود.",
                "Un voyage hivernal vers le nord devient une épreuve intime sur l'amitié, la distance et les promesses tenues.",
                "drama", "northbound", LocalDate.of(2026, 3, 6), 101, "7.6", 4890);
            create(movieService, genreRepository, "The Last Orbit", "المدار الأخير", "La dernière orbite",
                "The final crew of an aging station must choose between a safe return and one last scientific mission.",
                "طاقم المحطة الأخير يختار بين العودة الآمنة ومهمة علمية أخيرة.",
                "Le dernier équipage d'une station vieillissante doit choisir entre un retour sûr et une ultime mission scientifique.",
                "sci-fi", "the-last-orbit", LocalDate.of(2026, 2, 14), 132, "8.2", 12360);
            create(movieService, genreRepository, "Hidden Frequency", "التردد الخفي", "Fréquence cachée",
                "A late-night radio host receives calls from a frequency that does not officially exist.",
                "مذيع إذاعي ليلي يتلقى مكالمات من تردد لا وجود له رسميًا.",
                "Un animateur radio de nuit reçoit des appels provenant d'une fréquence qui n'existe officiellement pas.",
                "horror", "hidden-frequency", LocalDate.of(2026, 1, 30), 96, "7.7", 7310);
            create(movieService, genreRepository, "Small Worlds", "عوالم صغيرة", "Petits mondes",
                "A young inventor creates miniature living worlds and discovers that one of them is trying to communicate back.",
                "مخترع شاب يصنع عوالم حية مصغرة ويكتشف أن أحدها يحاول التواصل معه.",
                "Un jeune inventeur crée des mondes miniatures vivants et découvre que l'un d'eux tente de communiquer avec lui.",
                "animation", "small-worlds", LocalDate.of(2025, 12, 19), 94, "8.0", 9570);
        };
    }

    private static void create(MovieService movieService, GenreRepository genreRepository,
                               String en, String ar, String fr,
                               String enDescription, String arDescription, String frDescription,
                               String genreSlug, String assetSlug, LocalDate date, int runtime, String rating, int votes) {
        Genre genre = genreRepository.findBySlug(genreSlug).orElseThrow();
        MovieForm form = new MovieForm();
        form.setOriginalTitle(en);
        form.setOriginalLanguage("en");
        form.setReleaseDate(date);
        form.setRuntimeMinutes(runtime);
        form.setVoteAverage(new BigDecimal(rating));
        form.setVoteCount(votes);
        form.setStatus(MovieStatus.PUBLISHED);
        form.setGenreIds(List.of(genre.getId()));
        form.setPosterUrl("/assets/img/posters/" + assetSlug + ".png");
        form.setBackdropUrl("/assets/img/backdrops/" + assetSlug + ".svg");
        setTranslation(form, "en", en, enDescription);
        setTranslation(form, "fr", fr, frDescription);
        setTranslation(form, "ar", ar, arDescription);
        movieService.create(form);
    }

    private static void setTranslation(MovieForm form, String locale, String title, String description) {
        MovieForm.TranslationInput input = form.getTranslations().stream().filter(t -> locale.equals(t.getLocale())).findFirst().orElseThrow();
        input.setTitle(title);
        input.setDescription(description);
        input.setShortDescription(description.length() > 220 ? description.substring(0, 217) + "..." : description);
        input.setSeoTitle(title + " - Cinema One");
        input.setSeoDescription(description.length() > 300 ? description.substring(0, 297) + "..." : description);
    }
}
