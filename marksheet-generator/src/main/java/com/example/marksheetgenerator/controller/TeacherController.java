package com.example.marksheetgenerator.controller;

import com.example.marksheetgenerator.model.Marksheet;
import com.example.marksheetgenerator.service.MarksheetService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TeacherController {
    private final MarksheetService marksheetService;

    public TeacherController(MarksheetService marksheetService) {
        this.marksheetService = marksheetService;
    }

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
        // Prepopulate the class based on the teacher.
        marksheet.setClassName(assignedClass);
        // Prepopulate the roll number based on the teacher's next available number.
        String nextRollNo = marksheetService.getNextRollNumber(teacherUsername);
        marksheet.setRollNumber(nextRollNo);
        model.addAttribute("marksheet", marksheet);
        return "marksheet_form";
    }

    // Teacher-specific endpoint that returns only marksheets for the teacher's
    // assigned class.
    // Supports an optional search parameter.
    @GetMapping("/teacher/marksheets")
    public String listTeacherMarksheets(Authentication authentication,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        String teacherUsername = authentication.getName();
        String assignedClass = "";
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
        List<Marksheet> marksheets = marksheetService.getMarksheetsByClass(assignedClass);
        // If a search term is provided, filter further by student name or roll number.
        if (search != null && !search.trim().isEmpty()) {
            marksheets = marksheets.stream()
                    .filter(m -> m.getStudentName().toLowerCase().contains(search.toLowerCase())
                            || m.getRollNumber().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }
        model.addAttribute("marksheets", marksheets);
        return "marksheet_list";
    }
}