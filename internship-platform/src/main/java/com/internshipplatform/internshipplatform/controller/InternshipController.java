package com.internshipplatform.internshipplatform.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.internshipplatform.internshipplatform.dto.*;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.InternshipService;
import com.internshipplatform.internshipplatform.service.StudentInternshipService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internships")
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;
    private final StudentInternshipService studentInternshipService;
    private final JwtUtil jwtUtil;

    // ---------------- PUBLIC ENDPOINTS ----------------

    // List all internships (no login required)
    @GetMapping
    public List<InternshipResponseDTO> getAllInternships() {
        return internshipService.getAllPublicInternships();
    }

    // Search internships (public)
    @GetMapping("/search")
    public Page<InternshipResponseDTO> searchInternships(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String deadlineFrom,
            @RequestParam(required = false) String deadlineTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return internshipService.searchInternships(
                keyword, location, type, skill,
                deadlineFrom, deadlineTo,
                page, size, sortBy, sortDir
        );
    }

    // Public details of a single internship
    @GetMapping("/{id}")
    public InternshipResponseDTO getInternshipById(@PathVariable Long id) {
        return internshipService.getInternshipById(id);
    }

    // ---------------- COMPANY ENDPOINTS ----------------

    // View my company’s internships
    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping("/my")
    public List<InternshipResponseDTO> getMyInternships(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return internshipService.getMyInternships(userId);
    }

    // Create internship
    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InternshipResponseDTO createInternship(
            HttpServletRequest request,
            @Valid @RequestBody InternshipRequestDTO body
    ) {
        Long companyUserId = jwtUtil.getUserIdFromRequest(request);
        return internshipService.createInternship(companyUserId, body);
    }

    // Get a specific internship that belongs to logged-in company
    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping("/my/{id}")
    public InternshipResponseDTO getMyInternship(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long companyUserId = jwtUtil.getUserIdFromRequest(request);
        return internshipService.getMyInternshipById(id, companyUserId);
    }

    // Update internship that belongs to logged-in company
    @PreAuthorize("hasRole('COMPANY')")
    @PutMapping("/my/{id}")
    public InternshipResponseDTO updateMyInternship(
            @PathVariable Long id,
            @Valid @RequestBody InternshipUpdateRequest updateRequest,
            HttpServletRequest request
    ) {
        Long companyUserId = jwtUtil.getUserIdFromRequest(request);
        return internshipService.updateMyInternship(id, companyUserId, updateRequest);
    }

    // Delete internship that belongs to logged-in company
    @PreAuthorize("hasRole('COMPANY')")
    @DeleteMapping("/my/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyInternship(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long companyUserId = jwtUtil.getUserIdFromRequest(request);
        internshipService.deleteMyInternship(id, companyUserId);
    }

    // View applications for one of my internships
    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping("/{id}/applications")
    public List<ApplicationResponseDTO> getApplicationsForInternship(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long companyUserId = jwtUtil.getUserIdFromRequest(request);
        return studentInternshipService.getApplicationsForInternship(id, companyUserId);
    }

    // ---------------- STUDENT-SAVED & APPLICATION ENDPOINTS ----------------

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{id}/save")
    public ResponseEntity<?> saveInternship(
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        Long userId = jwtUtil.getUserIdFromRequest(http);
        studentInternshipService.saveInternship(id, userId);
        return ResponseEntity.ok("Internship saved");
    }

    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{id}/save")
    public ResponseEntity<?> unsaveInternship(
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        Long userId = jwtUtil.getUserIdFromRequest(http);
        studentInternshipService.unsaveInternship(id, userId);
        return ResponseEntity.ok("Internship unsaved");
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/saved/my")
    public List<InternshipResponseDTO> getMySavedInternships(HttpServletRequest http) {
        Long userId = jwtUtil.getUserIdFromRequest(http);
        return studentInternshipService.getMySavedInternships(userId);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{id}/apply")
    public ResponseEntity<?> applyToInternship(
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        Long userId = jwtUtil.getUserIdFromRequest(http);
        studentInternshipService.applyToInternship(id, userId);
        return ResponseEntity.ok("Application submitted");
    }

    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{id}/apply")
    public ResponseEntity<?> withdrawApplication(
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        Long userId = jwtUtil.getUserIdFromRequest(http);
        studentInternshipService.withdrawApplication(id, userId);
        return ResponseEntity.ok("Application withdrawn");
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/applied/my")
    public List<ApplicationResponseDTO> getMyApplications(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return studentInternshipService.getMyApplications(userId);
    }
}
