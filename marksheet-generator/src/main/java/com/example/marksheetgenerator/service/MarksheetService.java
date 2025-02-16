package com.example.marksheetgenerator.service;

import com.example.marksheetgenerator.model.Marksheet;
import com.example.marksheetgenerator.repository.MarksheetRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MarksheetService {

    private final MarksheetRepository marksheetRepository;

    public MarksheetService(MarksheetRepository marksheetRepository) {
        this.marksheetRepository = marksheetRepository;
    }

    // Create or Update a Marksheet
    public Marksheet generateAndSaveMarksheet(Marksheet marksheet) {
        marksheet.calculateResults();
        return marksheetRepository.save(marksheet);
    }

    // Retrieve all Marksheets
    public List<Marksheet> getAllMarksheets() {
        return marksheetRepository.findAll();
    }

    // Find a Marksheet by its ID
    public Marksheet getMarksheetById(String id) {
        return marksheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marksheet not found with id: " + id));
    }

    // Search by student name or roll number
    public List<Marksheet> searchMarksheets(String query) {
        List<Marksheet> byName = marksheetRepository.findByStudentNameContainingIgnoreCase(query);
        List<Marksheet> byRoll = marksheetRepository.findByRollNumberContainingIgnoreCase(query);

        // Combine results into a set to avoid duplicates
        Set<Marksheet> combined = new HashSet<>(byName);
        combined.addAll(byRoll);
        return new ArrayList<>(combined);
    }

    public Map<String, Object> getDashboardStats() {
        List<Marksheet> list = getAllMarksheets();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", list.size());
        double totalMath = list.stream().mapToInt(Marksheet::getMath).sum();
        double totalScience = list.stream().mapToInt(Marksheet::getScience).sum();
        double totalEnglish = list.stream().mapToInt(Marksheet::getEnglish).sum();
        stats.put("avgMath", list.size() > 0 ? String.format("%.2f", totalMath / list.size()) : 0);
        stats.put("avgScience", list.size() > 0 ? String.format("%.2f", totalScience / list.size()) : 0);
        stats.put("avgEnglish", list.size() > 0 ? String.format("%.2f", totalEnglish / list.size()) : 0);
        return stats;
    }

    // Delete a Marksheet
    public void deleteMarksheetById(String id) {
        marksheetRepository.deleteById(id);
    }
}
