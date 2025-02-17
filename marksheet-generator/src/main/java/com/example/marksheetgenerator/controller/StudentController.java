package com.example.marksheetgenerator.controller;

import com.example.marksheetgenerator.model.Marksheet;
import com.example.marksheetgenerator.repository.MarksheetRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {

    private final MarksheetRepository marksheetRepository;

    public StudentController(MarksheetRepository marksheetRepository) {
        this.marksheetRepository = marksheetRepository;
    }

    @GetMapping("/student/login")
    public String showStudentLogin() {
        return "student_login";
    }

    @GetMapping("/student/marksheet")
    public String showStudentMarksheet(Authentication auth, Model model) {
        // Get roll number from authenticated user's username
        User user = (User) auth.getPrincipal();
        String rollNumber = user.getUsername();

        // Find the marksheet for this student (assuming one marksheet per student)
        Marksheet marksheet = marksheetRepository.findAll()
                .stream()
                .filter(m -> rollNumber.equals(m.getRollNumber()))
                .findFirst()
                .orElse(null);

        model.addAttribute("marksheet", marksheet);
        return "marksheet_view";
    }
}
