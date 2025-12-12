package com.empmgmt.service;

import com.empmgmt.model.OnboardingFlow;
import com.empmgmt.model.OnboardingTask;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface OnboardingService {

    // FLOW
    OnboardingFlow startOnboarding(Long employeeId);
    List<OnboardingFlow> getAllFlows();

    // TASKS
    List<OnboardingTask> getTasks(Long employeeId);
    OnboardingTask getTask(Long taskId);
    OnboardingTask addTask(OnboardingTask task);

    // These now return Long (employeeId) — consistent with controller usage
    Long updateTask(Long taskId, OnboardingTask updated);
    Long completeTask(Long taskId);
    Long deleteTask(Long taskId);

    // UPLOADS
    void uploadDocument(Long taskId, String filePath);
    void uploadFile(Long taskId, MultipartFile file);

    // PROGRESS
    int getProgress(Long employeeId);

    // UTIL
    Long getEmployeeIdFromPrincipal(Principal principal);
}