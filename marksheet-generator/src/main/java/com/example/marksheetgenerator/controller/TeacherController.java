package com.example.marksheetgenerator.controller;

import com.example.marksheetgenerator.model.Marksheet;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeacherController {

    @GetMapping("/teacher/login")
    public String showTeacherLogin() {
        return "teacher_login";
    }

    @GetMapping("/teacher/addMarksheet")
    public String addMarksheet(Model model, Authentication authentication) {
        String teacherUsername = authentication.getName();
        String assignedClass = "";
        // Map teacher usernames to their assigned classes.
        switch (teacherUsername) {
            case "teacher1":
                assignedClass = "10th";
                break;
            case "teacher2":
                assignedClass = "11th";
                break;
            case "teacher3":
                assignedClass = "12th";
                break;
            default:
                assignedClass = "";
        }
        Marksheet marksheet = new Marksheet();
        // Prepopulate the className based on the teacher.
        marksheet.setClassName(assignedClass);
        model.addAttribute("marksheet", marksheet);
        return "marksheet_form";
    }
}
