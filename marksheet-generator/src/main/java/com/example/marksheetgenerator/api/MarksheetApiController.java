package com.example.marksheetgenerator.api;

import com.example.marksheetgenerator.model.Marksheet;
import com.example.marksheetgenerator.service.MarksheetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MarksheetApiController {

    private final MarksheetService marksheetService;

    public MarksheetApiController(MarksheetService marksheetService) {
        this.marksheetService = marksheetService;
    }

    @GetMapping("/api/marksheets")
    public List<Marksheet> getMarksheets() {
        return marksheetService.getAllMarksheets();
    }
}
