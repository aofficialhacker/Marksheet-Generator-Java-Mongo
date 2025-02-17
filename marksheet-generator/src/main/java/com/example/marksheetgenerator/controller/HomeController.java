package com.example.marksheetgenerator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        // Return the landing page template (index.html)
        return "index";
    }
}
