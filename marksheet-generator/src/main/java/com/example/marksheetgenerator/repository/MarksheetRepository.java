package com.example.marksheetgenerator.repository;

import com.example.marksheetgenerator.model.Marksheet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarksheetRepository extends MongoRepository<Marksheet, String> {

    // Search by partial match on studentName (case-insensitive)
    List<Marksheet> findByStudentNameContainingIgnoreCase(String name);

    // Search by partial match on rollNumber (case-insensitive)
    List<Marksheet> findByRollNumberContainingIgnoreCase(String rollNumber);
}
