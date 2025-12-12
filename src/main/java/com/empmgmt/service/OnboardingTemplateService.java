package com.empmgmt.service;

import com.empmgmt.model.OnboardingTemplateTask;
import java.util.List;

public interface OnboardingTemplateService {
    List<OnboardingTemplateTask> getTemplates();
    OnboardingTemplateTask create(OnboardingTemplateTask t);
    void delete(Long id);
}
