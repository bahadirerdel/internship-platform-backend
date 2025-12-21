package com.internshipplatform.internshipplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public void sendEmail(String to, String subject, String content) {
        // Demo: print to console instead of SMTP
        log.info("=== EMAIL (DEMO) ===");
        log.info("To: {}", to);
        log.info("Subject: {}", subject);
        log.info("Content:\n{}", content);
        log.info("====================");
    }
}
