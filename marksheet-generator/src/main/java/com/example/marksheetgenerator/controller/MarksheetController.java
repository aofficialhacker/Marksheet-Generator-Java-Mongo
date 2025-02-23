package com.example.marksheetgenerator.controller;

import com.example.marksheetgenerator.model.Marksheet;
import com.example.marksheetgenerator.model.StudentUser;
import com.example.marksheetgenerator.repository.StudentUserRepository;
import com.example.marksheetgenerator.service.MarksheetService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.io.PrintWriter;
import java.io.IOException;

@Controller
public class MarksheetController {

    private final MarksheetService marksheetService;
    private final StudentUserRepository studentUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public MarksheetController(MarksheetService marksheetService,
            StudentUserRepository studentUserRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.marksheetService = marksheetService;
        this.studentUserRepository = studentUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/generate")
    public String generateMarksheet(@Valid @ModelAttribute("marksheet") Marksheet marksheet,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "marksheet_form";
        }

        // Save the marksheet and display it
        Marksheet savedMarksheet = marksheetService.generateAndSaveMarksheet(marksheet);
        model.addAttribute("marksheet", savedMarksheet);

        // Create a student user record if it doesn't exist
        String rollNumber = savedMarksheet.getRollNumber();
        if (!studentUserRepository.existsById(rollNumber)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            String dobStr = savedMarksheet.getDob().atStartOfDay(ZoneOffset.UTC).format(formatter);
            String hashedPassword = passwordEncoder.encode(dobStr);
            StudentUser newUser = new StudentUser(rollNumber, hashedPassword);
            studentUserRepository.save(newUser);
        }

        return "marksheet_view";
    }

    @GetMapping("/marksheets")
    public String listMarksheets(@RequestParam(name = "search", required = false) String search,
            Model model,
            Authentication authentication) {
        List<Marksheet> marksheets;
        if (authentication != null &&
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            String teacherUsername = authentication.getName();
            String assignedClass = switch (teacherUsername) {
                case "teacher1" -> "10th";
                case "teacher2" -> "11th";
                case "teacher3" -> "12th";
                default -> "";
            };

            final String finalAssignedClass = assignedClass;
            marksheets = (search != null && !search.trim().isEmpty())
                    ? marksheetService.searchMarksheets(search).stream()
                            .filter(ms -> finalAssignedClass.equals(ms.getClassName()))
                            .collect(Collectors.toList())
                    : marksheetService.getAllMarksheets().stream()
                            .filter(ms -> finalAssignedClass.equals(ms.getClassName()))
                            .collect(Collectors.toList());
        } else {
            marksheets = (search != null && !search.trim().isEmpty())
                    ? marksheetService.searchMarksheets(search)
                    : marksheetService.getAllMarksheets();
        }
        model.addAttribute("marksheets", marksheets);
        return "marksheet_list";
    }

    @GetMapping("/marksheets/edit/{id}")
    public String editMarksheet(@PathVariable String id, Model model) {
        Marksheet existing = marksheetService.getMarksheetById(id);
        model.addAttribute("marksheet", existing);
        return "marksheet_form";
    }

    @PostMapping("/marksheets/delete/{id}")
    public String deleteMarksheet(@PathVariable String id) {
        marksheetService.deleteMarksheetById(id);
        return "redirect:/marksheets";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        List<Marksheet> marksheets = marksheetService.getAllMarksheets();
        if (authentication != null &&
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            String teacherUsername = authentication.getName();
            String assignedClass = switch (teacherUsername) {
                case "teacher1" -> "10th";
                case "teacher2" -> "11th";
                case "teacher3" -> "12th";
                default -> "";
            };

            final String finalAssignedClass = assignedClass;
            marksheets = marksheets.stream()
                    .filter(ms -> finalAssignedClass.equals(ms.getClassName()))
                    .collect(Collectors.toList());
        }
        model.addAttribute("stats", marksheetService.computeDashboardData(marksheets));
        return "dashboard";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/marksheets/export")
    public void exportCSV(HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("text/csv");
        String filename = "marksheets.csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<Marksheet> marksheets;
        if (authentication != null &&
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            String teacherUsername = authentication.getName();
            String assignedClass;
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
            marksheets = marksheetService.getMarksheetsByClass(assignedClass);
        } else {
            marksheets = marksheetService.getAllMarksheets();
        }
        PrintWriter writer = response.getWriter();
        writer.println("Student Name,Roll Number,Class,Date of Birth,Math,Science,English,Total,Percentage,Grade");
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

    @GetMapping("/marksheets/view/{id}")
    public String viewMarksheet(@PathVariable String id, Model model) {
        Marksheet marksheet = marksheetService.getMarksheetById(id);
        model.addAttribute("marksheet", marksheet);
        return "marksheet_view";
    }
}
