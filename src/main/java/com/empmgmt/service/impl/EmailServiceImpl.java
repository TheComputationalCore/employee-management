package com.empmgmt.service.impl;

import com.empmgmt.model.EmailLog;
import com.empmgmt.repository.EmailLogRepository;
import com.empmgmt.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailLogRepository repo;

    @Override
    public void logEmail(String to, String subject, String body) {

        EmailLog log = EmailLog.builder()
                .toEmail(to)
                .subject(subject)
                .body(body)
                .timestamp(LocalDateTime.now())
                .build();

        repo.save(log);
    }
}
