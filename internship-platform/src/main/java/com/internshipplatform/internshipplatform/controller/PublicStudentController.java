package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.StudentPublicProfileDTO;
import com.internshipplatform.internshipplatform.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class PublicStudentController {

    private final StudentService studentService;

    @GetMapping("/{studentUserId}/public")
    public StudentPublicProfileDTO getPublicStudent(@PathVariable Long studentUserId) {
        return studentService.getPublicStudentProfile(studentUserId);
    }
}
