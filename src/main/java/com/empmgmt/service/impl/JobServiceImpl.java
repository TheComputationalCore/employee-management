package com.empmgmt.service.impl;

import com.empmgmt.model.Job;
import com.empmgmt.repository.JobRepository;
import com.empmgmt.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository repo;

    @Override
    public Job create(Job job) {
        job.setActive(true);
        return repo.save(job);
    }

    @Override
    public Job update(Long id, Job updated) {
        Job job = repo.findById(id).orElseThrow();
        job.setTitle(updated.getTitle());
        job.setDepartment(updated.getDepartment());
        job.setEmploymentType(updated.getEmploymentType());
        job.setLocation(updated.getLocation());
        job.setDescription(updated.getDescription());
        job.setExperienceRequired(updated.getExperienceRequired());
        job.setSalaryMin(updated.getSalaryMin());
        job.setSalaryMax(updated.getSalaryMax());
        return repo.save(job);
    }

    @Override
    public List<Job> getAll() {
        return repo.findAll();
    }

    @Override
    public List<Job> getActiveJobs() {
        return repo.findByActiveTrue();
    }

    @Override
    public Job getById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    @Override
    public void close(Long id) {
        Job j = repo.findById(id).orElseThrow();
        j.setActive(false);
        repo.save(j);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }


}
