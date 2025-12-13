package com.internshipplatform.internshipplatform.dto;

public class ResumeFileDto {
    private org.springframework.core.io.Resource resource;
    private String fileName;
    private String contentType;

    public org.springframework.core.io.Resource getResource() {
        return resource;
    }

    public void setResource(org.springframework.core.io.Resource resource) {
        this.resource = resource;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
