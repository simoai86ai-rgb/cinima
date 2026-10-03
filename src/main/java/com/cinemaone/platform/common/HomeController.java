package com.cinemaone.platform.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/phase-1")
    String phaseOneLanding() { return "phase-1"; }
}
