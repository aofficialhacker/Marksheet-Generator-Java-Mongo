package com.example.marksheetgenerator.service;

import com.example.marksheetgenerator.model.StudentUser;
import com.example.marksheetgenerator.repository.StudentUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
public class StudentUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentUserRepository studentUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        StudentUser student = studentUserRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found with roll number: " + username));

        return new User(student.getRollNumber(), student.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")));
    }
}
