package com.cinemaone.platform.admin;

import com.cinemaone.platform.genre.GenreForm;
import com.cinemaone.platform.genre.GenreService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/genres")
public class AdminGenreController {
    private final GenreService service;

    public AdminGenreController(GenreService service) { this.service = service; }

    @GetMapping
    String list(Model model) {
        if (!model.containsAttribute("genreForm")) model.addAttribute("genreForm", new GenreForm());
        model.addAttribute("genres", service.findAll());
        return "admin/genres";
    }

    @PostMapping
    String create(@Valid @ModelAttribute GenreForm genreForm,
                  BindingResult bindingResult,
                  Model model,
                  RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            try {
                service.create(genreForm);
                redirectAttributes.addFlashAttribute("success", "Genre created.");
                return "redirect:/admin/genres";
            } catch (IllegalArgumentException ex) {
                bindingResult.reject("genre.invalid", ex.getMessage());
            }
        }
        model.addAttribute("genres", service.findAll());
        return "admin/genres";
    }

    @PostMapping("/{id}/delete")
    String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("success", "Genre deleted.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/genres";
    }
}
