package com.example.marksheetgenerator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a student user.
 * The rollNumber is used as the username.
 * The password should be the BCrypt hash of the student's DOB in ddmmyyyy
 * format.
 */
@Document(collection = "student_users")
public class StudentUser {

    @Id
    private String rollNumber;
    private String password;

    public StudentUser() {
    }

    public StudentUser(String rollNumber, String password) {
        this.rollNumber = rollNumber;
        this.password = password;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
