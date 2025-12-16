package com.empmgmt.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String root(Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/web/dashboard";
        }

        return "redirect:/login";
    }
}
