package com.example.marksheetgenerator.repository;

import com.example.marksheetgenerator.model.Marksheet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MarksheetRepository extends MongoRepository<Marksheet, String> {

    // Existing search methods
    List<Marksheet> findByStudentNameContainingIgnoreCase(String name);

    List<Marksheet> findByRollNumberContainingIgnoreCase(String rollNumber);

    // New method: using a custom query to match className case-insensitively
    @Query("{'className': {$regex: ?0, $options: 'i'}}")
    List<Marksheet> findByClassNameIgnoreCase(String className);
}
