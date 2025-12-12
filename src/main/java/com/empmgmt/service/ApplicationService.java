package com.empmgmt.service;

import com.empmgmt.model.Application;

import java.util.List;

public interface ApplicationService {
    Application submit(Application app);
    List<Application> getByJob(Long jobId);
    Application updateStatus(Long id, String status);
    Application getById(Long id);

    void save(Application app);
}
