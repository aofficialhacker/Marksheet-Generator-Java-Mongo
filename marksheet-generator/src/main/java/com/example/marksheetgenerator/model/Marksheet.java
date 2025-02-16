package com.example.marksheetgenerator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
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
    private LocalDate dob;

    @Min(value = 0, message = "Marks cannot be less than 0")
    @Max(value = 100, message = "Marks cannot exceed 100")
    private int math;

    @Min(value = 0, message = "Marks cannot be less than 0")
    @Max(value = 100, message = "Marks cannot exceed 100")
    private int science;

    @Min(value = 0, message = "Marks cannot be less than 0")
    @Max(value = 100, message = "Marks cannot exceed 100")
    private int english;

    private int total;
    private double percentage;
    private String grade;

    // New field for profile picture
    private String profilePicture;

    public Marksheet() {
    }

    public Marksheet(String studentName, String rollNumber, String className, LocalDate dob,
            int math, int science, int english) {
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.className = className;
        this.dob = dob;
        this.math = math;
        this.science = science;
        this.english = english;
        calculateResults();
    }

    public void calculateResults() {
        this.total = this.math + this.science + this.english;
        this.percentage = total / 3.0;
        this.grade = (percentage >= 75) ? "A" : (percentage >= 60) ? "B" : (percentage >= 40) ? "C" : "D";
    }

    // Getters and setters

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
}
