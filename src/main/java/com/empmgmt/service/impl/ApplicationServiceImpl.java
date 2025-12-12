package com.empmgmt.service.impl;

import com.empmgmt.model.Application;
import com.empmgmt.repository.ApplicationRepository;
import com.empmgmt.service.ApplicationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository repo;

    @Override
    public Application submit(Application app) {
        // ensure default state
        if (app.getStatus() == null) {
            app.setStatus("Applied");
        }
        return repo.save(app);
    }

    @Override
    public List<Application> getByJob(Long jobId) {
        return repo.findByJobId(jobId);
    }

    @Override
    @Transactional
    public Application updateStatus(Long id, String status) {
        // 🔥 FIX: Only update the status column
        repo.updateStatusOnly(id, status);

        // Fetch updated application (safe return)
        Application updated = repo.findById(id).orElseThrow();

        return updated;
    }

    @Override
    public Application getById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    @Override
    public void save(Application app) {

    }
}
