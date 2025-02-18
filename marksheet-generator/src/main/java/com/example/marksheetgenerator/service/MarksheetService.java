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

    // New method to get marksheets filtered by class name using our custom query.
    public List<Marksheet> getMarksheetsByClass(String className) {
        return marksheetRepository.findByClassNameIgnoreCase(className);
    }

    public Map<String, Object> getDashboardData() {
        List<Marksheet> list = getAllMarksheets();
        Map<String, Object> data = new HashMap<>();
        data.put("totalStudents", list.size());

        if (list.isEmpty()) {
            data.put("avgMath", 0);
            data.put("avgScience", 0);
            data.put("avgEnglish", 0);
            data.put("topPerformer", "N/A");
            data.put("lowestPerformer", "N/A");
            data.put("passPercentage", "0.00");
            data.put("failPercentage", "0.00");
            data.put("recentMarksheets", new ArrayList<Marksheet>());
            return data;
        }

        double totalMath = list.stream().mapToInt(Marksheet::getMath).sum();
        double totalScience = list.stream().mapToInt(Marksheet::getScience).sum();
        double totalEnglish = list.stream().mapToInt(Marksheet::getEnglish).sum();

        data.put("avgMath", totalMath / list.size());
        data.put("avgScience", totalScience / list.size());
        data.put("avgEnglish", totalEnglish / list.size());

        Marksheet top = list.stream().max(Comparator.comparingInt(Marksheet::getTotal)).orElse(null);
        Marksheet low = list.stream().min(Comparator.comparingInt(Marksheet::getTotal)).orElse(null);
        data.put("topPerformer", top != null ? top.getStudentName() + " (" + top.getTotal() + ")" : "N/A");
        data.put("lowestPerformer", low != null ? low.getStudentName() + " (" + low.getTotal() + ")" : "N/A");

        long passCount = list.stream().filter(m -> !"D".equalsIgnoreCase(m.getGrade())).count();
        double passPercentage = ((double) passCount / list.size()) * 100;
        data.put("passPercentage", String.format("%.2f", passPercentage));
        data.put("failPercentage", String.format("%.2f", 100 - passPercentage));

        List<Marksheet> recent = new ArrayList<>(list);
        Collections.reverse(recent);
        if (recent.size() > 5) {
            recent = recent.subList(0, 5);
        }
        data.put("recentMarksheets", recent);

        return data;
    }

    public Map<String, Object> computeDashboardData(List<Marksheet> list) {
        Map<String, Object> data = new HashMap<>();
        data.put("totalStudents", list.size());
        if (list.isEmpty()) {
            data.put("avgMath", 0);
            data.put("avgScience", 0);
            data.put("avgEnglish", 0);
            data.put("topPerformer", "N/A");
            data.put("lowestPerformer", "N/A");
            data.put("passPercentage", "0.00");
            data.put("failPercentage", "0.00");
            data.put("recentMarksheets", new ArrayList<Marksheet>());
            return data;
        }
        double totalMath = list.stream().mapToInt(Marksheet::getMath).sum();
        double totalScience = list.stream().mapToInt(Marksheet::getScience).sum();
        double totalEnglish = list.stream().mapToInt(Marksheet::getEnglish).sum();
        data.put("avgMath", totalMath / list.size());
        data.put("avgScience", totalScience / list.size());
        data.put("avgEnglish", totalEnglish / list.size());
        Marksheet top = list.stream().max(Comparator.comparingInt(Marksheet::getTotal)).orElse(null);
        Marksheet low = list.stream().min(Comparator.comparingInt(Marksheet::getTotal)).orElse(null);
        data.put("topPerformer", top != null ? top.getStudentName() + " (" + top.getTotal() + ")" : "N/A");
        data.put("lowestPerformer", low != null ? low.getStudentName() + " (" + low.getTotal() + ")" : "N/A");
        long passCount = list.stream().filter(m -> !"D".equalsIgnoreCase(m.getGrade())).count();
        double passPercentage = ((double) passCount / list.size()) * 100;
        data.put("passPercentage", String.format("%.2f", passPercentage));
        data.put("failPercentage", String.format("%.2f", 100 - passPercentage));
        List<Marksheet> recent = new ArrayList<>(list);
        Collections.reverse(recent);
        if (recent.size() > 5) {
            recent = recent.subList(0, 5);
        }
        data.put("recentMarksheets", recent);
        return data;
    }
}
