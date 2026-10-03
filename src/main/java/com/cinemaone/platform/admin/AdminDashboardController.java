package com.cinemaone.platform.admin;

import com.cinemaone.platform.movie.MovieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {
    private final MovieService movieService;

    public AdminDashboardController(MovieService movieService) { this.movieService = movieService; }

    @GetMapping("/admin")
    String dashboard(Model model) {
        model.addAttribute("totalMovies", movieService.countAll());
        model.addAttribute("publishedMovies", movieService.countPublished());
        model.addAttribute("draftMovies", movieService.countDrafts());
        model.addAttribute("archivedMovies", movieService.countArchived());
        model.addAttribute("recentMovies", movieService.search("", 0, 5).getContent());
        return "admin/dashboard";
    }
}
