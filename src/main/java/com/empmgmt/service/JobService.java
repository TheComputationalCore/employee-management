package com.empmgmt.service;

import com.empmgmt.model.Job;
import java.util.List;

public interface JobService {
    Job create(Job job);
    Job update(Long id, Job job);
    List<Job> getAll();
    List<Job> getActiveJobs();
    Job getById(Long id);
    void close(Long id);

    void delete(Long id);

}
