package com.internshipplatform.internshipplatform.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ResumeStorageService {

    private final Path root = Paths.get("uploads", "resumes");

    public ResumeStorageService() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize resume storage", e);
        }
    }

    public String saveResume(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Resume file is required");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";

        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot);

        // Unique file name to avoid collisions
        String storedName = "u" + userId + "_" + UUID.randomUUID() + ext;

        try {
            Path target = root.resolve(storedName).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return storedName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store resume", e);
        }
    }

    public Resource loadAsResource(String storedFileName) {
        if (storedFileName == null || storedFileName.isBlank()) {
            throw new RuntimeException("Resume not found");
        }

        try {
            Path filePath = root.resolve(storedFileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Resume not found");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Resume not found", e);
        }
    }

    public void delete(String storedFileName) {
        if (storedFileName == null || storedFileName.isBlank()) return;

        try {
            Files.deleteIfExists(root.resolve(storedFileName).normalize());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete resume file", e);
        }
    }
}
