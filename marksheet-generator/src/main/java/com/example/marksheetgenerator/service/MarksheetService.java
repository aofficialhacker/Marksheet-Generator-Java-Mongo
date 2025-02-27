package com.example.marksheetgenerator.service;

import com.example.marksheetgenerator.model.Marksheet;
import com.example.marksheetgenerator.repository.MarksheetRepository;
import com.example.marksheetgenerator.repository.StudentUserRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MarksheetService {
    private final MarksheetRepository marksheetRepository;
    private final StudentUserRepository studentUserRepository;

    public MarksheetService(MarksheetRepository marksheetRepository, StudentUserRepository studentUserRepository) {
        this.marksheetRepository = marksheetRepository;
        this.studentUserRepository = studentUserRepository;
    }

    public Marksheet generateAndSaveMarksheet(Marksheet marksheet) {
        // First calculate results (this may include total and percentage)
        marksheet.calculateResults();
        // Then update grade according to new grading criteria
        updateGrade(marksheet);
        if (marksheet.getId() != null && marksheet.getId().trim().isEmpty()) {
            marksheet.setId(null);
        }
        return marksheetRepository.save(marksheet);
    }

    // New method to update grade based on the new criteria.
    public void updateGrade(Marksheet marksheet) {
        double percentage = marksheet.getPercentage();
        if (percentage >= 90) {
            marksheet.setGrade("A+");
        } else if (percentage >= 80) {
            marksheet.setGrade("A");
        } else if (percentage >= 70) {
            marksheet.setGrade("B+");
        } else if (percentage >= 60) {
            marksheet.setGrade("B");
        } else if (percentage >= 50) {
            marksheet.setGrade("C");
        } else if (percentage >= 40) {
            marksheet.setGrade("D");
        } else {
            marksheet.setGrade("F");
        }
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
        Optional<Marksheet> marksheetOptional = marksheetRepository.findById(id);
        if (marksheetOptional.isPresent()) {
            Marksheet marksheet = marksheetOptional.get();
            String rollNumber = marksheet.getRollNumber();
            marksheetRepository.deleteById(id);
            if (studentUserRepository.existsById(rollNumber)) {
                studentUserRepository.deleteById(rollNumber);
            }
        } else {
            throw new RuntimeException("Marksheet not found with id: " + id);
        }
    }

    public List<Marksheet> getMarksheetsByClass(String className) {
        return marksheetRepository.findByClassNameIgnoreCase(className);
    }

    public String getNextRollNumber(String teacherUsername) {
        String prefix;
        if ("teacher1".equals(teacherUsername)) {
            prefix = "1";
        } else if ("teacher2".equals(teacherUsername)) {
            prefix = "2";
        } else if ("teacher3".equals(teacherUsername)) {
            prefix = "3";
        } else {
            throw new IllegalArgumentException("Invalid teacher username: " + teacherUsername);
        }
        int baseline = Integer.parseInt(prefix + "01");
        int nextRoll = baseline;
        List<Marksheet> marksheets = marksheetRepository.findAll();
        for (Marksheet ms : marksheets) {
            String roll = ms.getRollNumber();
            if (roll != null && roll.startsWith(prefix)) {
                try {
                    int rollNum = Integer.parseInt(roll);
                    if (rollNum >= nextRoll) {
                        nextRoll = rollNum + 1;
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid roll numbers.
                }
            }
        }
        return String.format("%03d", nextRoll);
    }

    // Updated method: compute grade distribution based on a provided list.
    public Map<String, Integer> getGradeDistribution(List<Marksheet> list) {
        Map<String, Integer> distribution = new HashMap<>();
        // Initialize with new grade keys
        distribution.put("A+", 0);
        distribution.put("A", 0);
        distribution.put("B+", 0);
        distribution.put("B", 0);
        distribution.put("C", 0);
        distribution.put("D", 0);
        distribution.put("F", 0);
        for (Marksheet m : list) {
            String grade = m.getGrade();
            distribution.put(grade, distribution.getOrDefault(grade, 0) + 1);
        }
        return distribution;
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
            data.put("gradeA+", 0);
            data.put("gradeA", 0);
            data.put("gradeB+", 0);
            data.put("gradeB", 0);
            data.put("gradeC", 0);
            data.put("gradeD", 0);
            data.put("gradeF", 0);
            return data;
        }
        double totalMath = list.stream().mapToInt(Marksheet::getMath).sum();
        double totalScience = list.stream().mapToInt(Marksheet::getScience).sum();
        double totalEnglish = list.stream().mapToInt(Marksheet::getEnglish).sum();
        data.put("avgMath", totalMath / list.size());
        data.put("avgScience", totalScience / list.size());
        data.put("avgEnglish", totalEnglish / list.size());

        // Compute top performers (handle ties)
        int maxTotal = list.stream().mapToInt(Marksheet::getTotal).max().orElse(0);
        List<Marksheet> topPerformers = list.stream()
                .filter(m -> m.getTotal() == maxTotal)
                .collect(Collectors.toList());
        String topStr = topPerformers.stream()
                .map(m -> m.getStudentName() + " (" + m.getTotal() + ")")
                .collect(Collectors.joining(", "));
        data.put("topPerformer", topStr.isEmpty() ? "N/A" : topStr);

        // Compute lowest performers (handle ties)
        int minTotal = list.stream().mapToInt(Marksheet::getTotal).min().orElse(0);
        List<Marksheet> lowPerformers = list.stream()
                .filter(m -> m.getTotal() == minTotal)
                .collect(Collectors.toList());
        String lowStr = lowPerformers.stream()
                .map(m -> m.getStudentName() + " (" + m.getTotal() + ")")
                .collect(Collectors.joining(", "));
        data.put("lowestPerformer", lowStr.isEmpty() ? "N/A" : lowStr);

        long passCount = list.stream().filter(m -> !m.getGrade().equalsIgnoreCase("F")).count();
        double passPercentage = ((double) passCount / list.size()) * 100;
        data.put("passPercentage", String.format("%.2f", passPercentage));
        data.put("failPercentage", String.format("%.2f", 100 - passPercentage));

        List<Marksheet> recent = new ArrayList<>(list);
        Collections.reverse(recent);
        if (recent.size() > 5) {
            recent = recent.subList(0, 5);
        }
        data.put("recentMarksheets", recent);

        // Compute grade distribution for the provided list (filtered by class for
        // teacher)
        Map<String, Integer> gradeDist = getGradeDistribution(list);
        data.put("gradeA+", gradeDist.get("A+"));
        data.put("gradeA", gradeDist.get("A"));
        data.put("gradeB+", gradeDist.get("B+"));
        data.put("gradeB", gradeDist.get("B"));
        data.put("gradeC", gradeDist.get("C"));
        data.put("gradeD", gradeDist.get("D"));
        data.put("gradeF", gradeDist.get("F"));

        return data;
    }

    public Map<String, Object> getDashboardData() {
        List<Marksheet> list = getAllMarksheets();
        return computeDashboardData(list);
    }
}
