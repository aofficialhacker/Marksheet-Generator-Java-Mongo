package com.example.marksheetgenerator.repository;

import com.example.marksheetgenerator.model.StudentUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentUserRepository extends MongoRepository<StudentUser, String> {
}
