package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.*;
import com.internshipplatform.internshipplatform.entity.Student;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.InterviewService;
import com.internshipplatform.internshipplatform.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final JwtUtil jwtUtil;
    private final InterviewService interviewService;


    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me")
    public StudentProfileResponse getMyProfile(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return studentService.getMyProfile(userId);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/me")
    public StudentProfileResponse updateMyProfile(
            HttpServletRequest request,
            @RequestBody @Valid StudentProfileUpdateRequest body
    ) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return studentService.updateMyProfile(userId, body);
    }
    // POST /api/students/me/resume
    @PostMapping(value = "/me/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMyResume(
            HttpServletRequest request,
            @RequestPart("file") MultipartFile file
    ) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        studentService.uploadResume(userId, file);
        return ResponseEntity.ok("Resume uploaded");
    }

    // GET /api/students/me/resume
    @GetMapping("/me/resume/download")
    public ResponseEntity<Resource> downloadMyResume(HttpServletRequest request) {

        Long userId = jwtUtil.getUserIdFromRequest(request);

        ResumeFileDto resume = studentService.downloadMyResume(userId);

        // ✅ SAFETY: clean filename before putting into header
        String safeFileName = resume.getFileName().replace("\"", "");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + safeFileName + "\"")
                .contentType(MediaType.parseMediaType(resume.getContentType()))
                .body(resume.getResource());
    }


    // DELETE /api/students/me/resume (optional)
    @DeleteMapping("/me/resume")
    public ResponseEntity<?> deleteMyResume(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        studentService.deleteResume(userId);
        return ResponseEntity.ok("Resume deleted");
    }
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me/interviews")
    public ResponseEntity<List<InterviewResponseDTO>> getMyInterviews(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return ResponseEntity.ok(interviewService.getMyStudentInterviews(userId));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me/strength")
    public ProfileStrengthResponseDTO getMyStrength(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return studentService.getMyProfileStrength(userId);
    }
}

