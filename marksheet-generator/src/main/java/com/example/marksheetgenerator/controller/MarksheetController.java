package com.example.marksheetgenerator.controller;

import com.example.marksheetgenerator.model.Marksheet;
import com.example.marksheetgenerator.service.MarksheetService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class MarksheetController {

    private final MarksheetService marksheetService;

    public MarksheetController(MarksheetService marksheetService) {
        this.marksheetService = marksheetService;
    }

    // Display the form (used for both creating a new Marksheet and editing an
    // existing one)
    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("marksheet", new Marksheet());
        return "marksheet_form";
    }

    // Process form submission for creating or updating a Marksheet
    @PostMapping("/generate")
    public String generateMarksheet(@Valid @ModelAttribute("marksheet") Marksheet marksheet,
            BindingResult result,
            @RequestParam("profilePictureFile") MultipartFile file,
            Model model) {
        if (result.hasErrors()) {
            return "marksheet_form";
        }

        // Handle file upload if a file is provided
        if (!file.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/uploads/";
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);
                java.nio.file.Files.createDirectories(path.getParent());
                java.nio.file.Files.write(path, file.getBytes());
                marksheet.setProfilePicture(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                // Optionally, add an error message to the model
            }
        }

        Marksheet savedMarksheet = marksheetService.generateAndSaveMarksheet(marksheet);
        model.addAttribute("marksheet", savedMarksheet);
        return "marksheet_view";
    }

    // List all marksheets, or search by query parameter
    @GetMapping("/marksheets")
    public String listMarksheets(@RequestParam(name = "search", required = false) String search, Model model) {
        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("marksheets", marksheetService.searchMarksheets(search));
        } else {
            model.addAttribute("marksheets", marksheetService.getAllMarksheets());
        }
        return "marksheet_list";
    }

    // Load an existing Marksheet for editing
    @GetMapping("/marksheets/edit/{id}")
    public String editMarksheet(@PathVariable String id, Model model) {
        Marksheet existing = marksheetService.getMarksheetById(id);
        model.addAttribute("marksheet", existing);
        return "marksheet_form"; // Reuse the same form for editing
    }

    // Delete a Marksheet
    @PostMapping("/marksheets/delete/{id}")
    public String deleteMarksheet(@PathVariable String id) {
        marksheetService.deleteMarksheetById(id);
        return "redirect:/marksheets";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("stats", marksheetService.getDashboardStats());
        model.addAttribute("gradeDistribution", marksheetService.getGradeDistribution());
        return "dashboard";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    // NEW: Export marksheets to CSV
    @GetMapping("/marksheets/export")
    public void exportCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        String filename = "marksheets.csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        // Retrieve all marksheets
        var marksheets = marksheetService.getAllMarksheets();
        PrintWriter writer = response.getWriter();
        // CSV header
        writer.println("Student Name,Roll Number,Class,Date of Birth,Math,Science,English,Total,Percentage,Grade");
        // CSV rows
        for (Marksheet m : marksheets) {
            writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%d,%d,%d,%d,%.2f,\"%s\"",
                    m.getStudentName(),
                    m.getRollNumber(),
                    m.getClassName(),
                    m.getDob() != null ? m.getDob().toString() : "",
                    m.getMath(),
                    m.getScience(),
                    m.getEnglish(),
                    m.getTotal(),
                    m.getPercentage(),
                    m.getGrade()));
        }
        writer.flush();
    }
}
