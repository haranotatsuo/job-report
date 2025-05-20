package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STAFF"))) {
            return "redirect:/reports/create";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_VIEWER"))) {
            return "redirect:/reports";
        }
        return "redirect:/login?unauthorized";
    }
}
