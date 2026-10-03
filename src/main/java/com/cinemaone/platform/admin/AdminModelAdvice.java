package com.cinemaone.platform.admin;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice(basePackages = "com.cinemaone.platform.admin")
public class AdminModelAdvice {
    @ModelAttribute("currentUserEmail")
    String currentUserEmail(Principal principal) {
        return principal == null ? null : principal.getName();
    }
}
