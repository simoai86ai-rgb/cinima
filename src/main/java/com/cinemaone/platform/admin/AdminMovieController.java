package com.cinemaone.platform.admin;

import com.cinemaone.platform.genre.GenreService;
import com.cinemaone.platform.movie.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/movies")
public class AdminMovieController {
    private final MovieService movieService;
    private final GenreService genreService;

    public AdminMovieController(MovieService movieService, GenreService genreService) {
        this.movieService = movieService;
        this.genreService = genreService;
    }

    @GetMapping
    String list(@RequestParam(defaultValue = "") String q,
                @RequestParam(defaultValue = "0") int page,
                Model model) {
        Page<Movie> movies = movieService.search(q, page, 20);
        model.addAttribute("movies", movies);
        model.addAttribute("q", q);
        return "admin/movies";
    }

    @GetMapping("/new")
    String createForm(Model model) {
        if (!model.containsAttribute("movieForm")) model.addAttribute("movieForm", new MovieForm());
        addFormReferenceData(model, "Add movie", null);
        return "admin/movie-form";
    }

    @PostMapping
    String create(@Valid @ModelAttribute("movieForm") MovieForm form,
                  BindingResult bindingResult,
                  Model model,
                  RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            try {
                Movie movie = movieService.create(form);
                redirectAttributes.addFlashAttribute("success", "Movie created successfully.");
                return "redirect:/admin/movies/" + movie.getId() + "/edit";
            } catch (IllegalArgumentException ex) {
                bindingResult.reject("movie.invalid", ex.getMessage());
            }
        }
        addFormReferenceData(model, "Add movie", null);
        return "admin/movie-form";
    }

    @GetMapping("/{id}/edit")
    String editForm(@PathVariable Long id, Model model) {
        Movie movie = movieService.get(id);
        if (!model.containsAttribute("movieForm")) model.addAttribute("movieForm", MovieForm.from(movie));
        addFormReferenceData(model, "Edit movie", id);
        return "admin/movie-form";
    }

    @PostMapping("/{id}")
    String update(@PathVariable Long id,
                  @Valid @ModelAttribute("movieForm") MovieForm form,
                  BindingResult bindingResult,
                  Model model,
                  RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            try {
                movieService.update(id, form);
                redirectAttributes.addFlashAttribute("success", "Movie updated successfully.");
                return "redirect:/admin/movies/" + id + "/edit";
            } catch (IllegalArgumentException ex) {
                bindingResult.reject("movie.invalid", ex.getMessage());
            }
        }
        addFormReferenceData(model, "Edit movie", id);
        return "admin/movie-form";
    }

    @PostMapping("/{id}/publish")
    String publish(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        movieService.setStatus(id, MovieStatus.PUBLISHED);
        redirectAttributes.addFlashAttribute("success", "Movie published.");
        return "redirect:/admin/movies";
    }

    @PostMapping("/{id}/archive")
    String archive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        movieService.setStatus(id, MovieStatus.ARCHIVED);
        redirectAttributes.addFlashAttribute("success", "Movie archived.");
        return "redirect:/admin/movies";
    }

    @PostMapping("/{id}/delete")
    String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        movieService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Movie permanently deleted.");
        return "redirect:/admin/movies";
    }

    private void addFormReferenceData(Model model, String pageTitle, Long movieId) {
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("statuses", MovieStatus.values());
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("movieId", movieId);
        model.addAttribute("activeNav", movieId == null ? "add" : "movies");
        model.addAttribute("formAction", movieId == null ? "/admin/movies" : "/admin/movies/" + movieId);
    }
}
