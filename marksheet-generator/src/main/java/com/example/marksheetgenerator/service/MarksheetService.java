package com.example.marksheetgenerator.service;

import com.example.marksheetgenerator.model.Marksheet;
import com.example.marksheetgenerator.repository.MarksheetRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MarksheetService {

    private final MarksheetRepository marksheetRepository;

    public MarksheetService(MarksheetRepository marksheetRepository) {
        this.marksheetRepository = marksheetRepository;
    }

    public Marksheet generateAndSaveMarksheet(Marksheet marksheet) {
        marksheet.calculateResults();
        return marksheetRepository.save(marksheet);
    }

    public List<Marksheet> getAllMarksheets() {
        return marksheetRepository.findAll();
    }

    public Marksheet getMarksheetById(String id) {
        return marksheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marksheet not found with id: " + id));
    }

    public List<Marksheet> searchMarksheets(String query) {
        List<Marksheet> byName = marksheetRepository.findByStudentNameContainingIgnoreCase(query);
        List<Marksheet> byRoll = marksheetRepository.findByRollNumberContainingIgnoreCase(query);
        Set<Marksheet> combined = new HashSet<>(byName);
        combined.addAll(byRoll);
        return new ArrayList<>(combined);
    }

    public void deleteMarksheetById(String id) {
        marksheetRepository.deleteById(id);
    }

    // New method for dashboard: get grade distribution
    public Map<String, Long> getGradeDistribution() {
        List<Marksheet> list = getAllMarksheets();
        return list.stream()
                .collect(Collectors.groupingBy(Marksheet::getGrade, Collectors.counting()));
    }

    // Existing method for dashboard statistics (if not defined, create one similar
    // to this)
    public Map<String, Object> getDashboardStats() {
        List<Marksheet> list = getAllMarksheets();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", list.size());
        double totalMath = list.stream().mapToInt(Marksheet::getMath).sum();
        double totalScience = list.stream().mapToInt(Marksheet::getScience).sum();
        double totalEnglish = list.stream().mapToInt(Marksheet::getEnglish).sum();
        stats.put("avgMath", list.size() > 0 ? totalMath / list.size() : 0);
        stats.put("avgScience", list.size() > 0 ? totalScience / list.size() : 0);
        stats.put("avgEnglish", list.size() > 0 ? totalEnglish / list.size() : 0);
        return stats;
    }
}
