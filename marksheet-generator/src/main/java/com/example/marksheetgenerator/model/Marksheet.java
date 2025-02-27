package com.example.marksheetgenerator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Document(collection = "marksheets")
public class Marksheet {

    @Id
    private String id;

    @NotBlank(message = "Student name is required")
    private String studentName;

    @NotBlank(message = "Roll number is required")
    private String rollNumber;

    @NotBlank(message = "Class name is required")
    private String className;

    @NotNull(message = "Date of birth is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;

    // For 10th class subjects
    @Min(0)
    @Max(100)
    private int math;

    @Min(0)
    @Max(100)
    private int science;

    @Min(0)
    @Max(100)
    private int english;

    @Min(0)
    @Max(100)
    private int hindi;

    @Min(0)
    @Max(100)
    private int marathi;

    @Min(0)
    @Max(100)
    private int history;

    @Min(0)
    @Max(100)
    private int geography;

    // For 11th/12th classes
    private String stream;

    @Min(0)
    @Max(100)
    private int physics;

    @Min(0)
    @Max(100)
    private int chemistry;

    @Min(0)
    @Max(100)
    private int biology;

    @Min(0)
    @Max(100)
    private int economics;

    @Min(0)
    @Max(100)
    private int businessStudies;

    @Min(0)
    @Max(100)
    private int accountancy;

    @Min(0)
    @Max(100)
    private int politicalScience;

    @Min(0)
    @Max(100)
    private int sociology;

    private int total;
    private double percentage;
    private String grade;

    // Profile picture (if needed)
    private String profilePicture;

    public Marksheet() {
    }

    public Marksheet(String studentName, String rollNumber, String className, LocalDate dob) {
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.className = className;
        this.dob = dob;
    }

    // Calculate total marks, percentage, and assign grade using the new criteria.
    public void calculateResults() {
        if ("10th".equals(className)) {
            // For 10th class, calculate over 7 subjects.
            this.total = math + science + english + hindi + marathi + history + geography;
            this.percentage = total / 7.0;
        } else if ("11th".equals(className) || "12th".equals(className)) {
            // For 11th/12th, include English for all streams.
            if ("Science".equalsIgnoreCase(stream)) {
                // Subjects: Math, Physics, Chemistry, Biology, English
                this.total = math + physics + chemistry + biology + english;
                this.percentage = total / 5.0;
            } else if ("Commerce".equalsIgnoreCase(stream)) {
                // Subjects: Math, Economics, Business Studies, Accountancy, English
                this.total = math + economics + businessStudies + accountancy + english;
                this.percentage = total / 5.0;
            } else if ("Arts".equalsIgnoreCase(stream)) {
                // Subjects: History, Political Science, Sociology, Geography, English
                this.total = history + politicalScience + sociology + geography + english;
                this.percentage = total / 5.0;
            } else {
                this.total = 0;
                this.percentage = 0;
            }
        }

        // Assign grade based on the updated criteria.
        if (percentage >= 90) {
            this.grade = "A+";
        } else if (percentage >= 80) {
            this.grade = "A";
        } else if (percentage >= 70) {
            this.grade = "B+";
        } else if (percentage >= 60) {
            this.grade = "B";
        } else if (percentage >= 50) {
            this.grade = "C";
        } else if (percentage >= 40) {
            this.grade = "D";
        } else {
            this.grade = "F";
        }
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public int getMath() {
        return math;
    }

    public void setMath(int math) {
        this.math = math;
    }

    public int getScience() {
        return science;
    }

    public void setScience(int science) {
        this.science = science;
    }

    public int getEnglish() {
        return english;
    }

    public void setEnglish(int english) {
        this.english = english;
    }

    public int getHindi() {
        return hindi;
    }

    public void setHindi(int hindi) {
        this.hindi = hindi;
    }

    public int getMarathi() {
        return marathi;
    }

    public void setMarathi(int marathi) {
        this.marathi = marathi;
    }

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }

    public int getGeography() {
        return geography;
    }

    public void setGeography(int geography) {
        this.geography = geography;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public int getPhysics() {
        return physics;
    }

    public void setPhysics(int physics) {
        this.physics = physics;
    }

    public int getChemistry() {
        return chemistry;
    }

    public void setChemistry(int chemistry) {
        this.chemistry = chemistry;
    }

    public int getBiology() {
        return biology;
    }

    public void setBiology(int biology) {
        this.biology = biology;
    }

    public int getEconomics() {
        return economics;
    }

    public void setEconomics(int economics) {
        this.economics = economics;
    }

    public int getBusinessStudies() {
        return businessStudies;
    }

    public void setBusinessStudies(int businessStudies) {
        this.businessStudies = businessStudies;
    }

    public int getAccountancy() {
        return accountancy;
    }

    public void setAccountancy(int accountancy) {
        this.accountancy = accountancy;
    }

    public int getPoliticalScience() {
        return politicalScience;
    }

    public void setPoliticalScience(int politicalScience) {
        this.politicalScience = politicalScience;
    }

    public int getSociology() {
        return sociology;
    }

    public void setSociology(int sociology) {
        this.sociology = sociology;
    }

    public int getTotal() {
        return total;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getGrade() {
        return grade;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
