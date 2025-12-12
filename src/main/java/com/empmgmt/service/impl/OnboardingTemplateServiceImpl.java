package com.empmgmt.service.impl;

import com.empmgmt.model.OnboardingTemplateTask;
import com.empmgmt.repository.OnboardingTemplateTaskRepository;
import com.empmgmt.service.OnboardingTemplateService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingTemplateServiceImpl implements OnboardingTemplateService {

    private final OnboardingTemplateTaskRepository repo;

    @Override
    public List<OnboardingTemplateTask> getTemplates() {
        return repo.findAll();
    }

    @Override
    public OnboardingTemplateTask create(OnboardingTemplateTask t) {
        return repo.save(t);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
