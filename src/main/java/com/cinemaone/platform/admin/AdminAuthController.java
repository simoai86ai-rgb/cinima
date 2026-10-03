package com.cinemaone.platform.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminAuthController {
    @GetMapping("/admin/login")
    String login() { return "admin/login"; }
}
