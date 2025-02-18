package com.example.marksheetgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class MarksheetGeneratorApplication {
    public static void main(String[] args) {
        // Set default timezone to Asia/Kolkata so that teacher-entered dates remain
        // intact.
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(MarksheetGeneratorApplication.class, args);
    }
}
