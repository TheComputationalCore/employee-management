package com.empmgmt.service.impl;

import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.model.OnboardingFlow;
import com.empmgmt.model.OnboardingTask;
import com.empmgmt.model.Employee;
import com.empmgmt.model.OnboardingTemplateTask;
import com.empmgmt.repository.EmployeeRepository;
import com.empmgmt.repository.OnboardingFlowRepository;
import com.empmgmt.repository.OnboardingTaskRepository;
import com.empmgmt.repository.OnboardingTemplateTaskRepository;
import com.empmgmt.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private final OnboardingTaskRepository taskRepo;
    private final OnboardingFlowRepository flowRepo;
    private final OnboardingTemplateTaskRepository templateRepo;
    private final EmployeeRepository employeeRepo;

    /* ============================================================
       START ONBOARDING FLOW
       ============================================================ */
    @Override
    public OnboardingFlow startOnboarding(Long employeeId) {
        OnboardingFlow existing = flowRepo.findByEmployeeId(employeeId).orElse(null);
        if (existing != null) return existing;

        OnboardingFlow flow = OnboardingFlow.builder()
                .employeeId(employeeId)
                .createdAt(LocalDate.now())
                .completed(false)
                .build();

        flow = flowRepo.save(flow);

        List<OnboardingTemplateTask> templates = templateRepo.findAll();
        for (OnboardingTemplateTask t : templates) {
            OnboardingTask task = OnboardingTask.builder()
                    .employeeId(employeeId)
                    .title(t.getTitle())
                    .description(t.getDescription())
                    .required(t.isRequired())
                    .assignedTo("HR Team")
                    .completed(false)
                    .dueDate(LocalDate.now().plusDays(7))
                    .build();
            taskRepo.save(task);
        }
        return flow;
    }

    @Override
    public List<OnboardingFlow> getAllFlows() {
        return flowRepo.findAll();
    }

    @Override
    public List<OnboardingTask> getTasks(Long employeeId) {
        return taskRepo.findByEmployeeId(employeeId);
    }

    @Override
    public OnboardingTask getTask(Long taskId) {
        return taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
    }

    @Override
    public OnboardingTask addTask(OnboardingTask task) {
        task.setCompleted(false);
        if (task.getDueDate() == null) {
            task.setDueDate(LocalDate.now().plusDays(7));
        }
        return taskRepo.save(task);
    }

    /* ============================================================
       UPDATE TASK — NOW RETURNS employeeId (Long)
       ============================================================ */
    @Override
    public Long updateTask(Long taskId, OnboardingTask updated) {
        OnboardingTask original = getTask(taskId);

        original.setTitle(updated.getTitle());
        original.setDescription(updated.getDescription());
        original.setRequired(updated.isRequired());
        original.setDueDate(updated.getDueDate());
        original.setAssignedTo(updated.getAssignedTo());

        taskRepo.save(original);
        return original.getEmployeeId(); // ← NOW RETURNS Long
    }

    /* ============================================================
       COMPLETE TASK — already correct
       ============================================================ */
    @Override
    public Long completeTask(Long taskId) {
        OnboardingTask task = getTask(taskId);
        task.setCompleted(true);
        taskRepo.save(task);
        updateFlowCompletionStatus(task.getEmployeeId());
        return task.getEmployeeId();
    }

    @Override
    public Long deleteTask(Long taskId) {
        OnboardingTask task = getTask(taskId);
        Long empId = task.getEmployeeId();
        taskRepo.delete(task);
        updateFlowCompletionStatus(empId);
        return empId;
    }

    @Override
    public void uploadDocument(Long taskId, String filePath) {
        OnboardingTask task = getTask(taskId);
        task.setFilePath(filePath);
        taskRepo.save(task);
    }

    /**
     * Safe file upload:
     *  - create directory if missing
     *  - validate original filename is present and does not contain traversal characters
     *  - only use the last path component and replace unsafe chars
     */
    @Override
    public void uploadFile(Long taskId, MultipartFile file) {
        try {
            String folder = "uploads/onboarding/";
            Files.createDirectories(Path.of(folder));

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()
                    || originalFilename.contains("..")
                    || originalFilename.contains("/") || originalFilename.contains("\\")) {
                throw new IllegalArgumentException("Invalid file name");
            }

            // Only take the last path component and strip unsafe characters.
            // Allowed characters: letters, numbers, dot, underscore, hyphen.
            String safeFileName = System.currentTimeMillis() + "-" +
                    Path.of(originalFilename).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_");

            Path filePath = Path.of(folder + safeFileName);
            Files.write(filePath, file.getBytes());

            uploadDocument(taskId, filePath.toString());
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    @Override
    public int getProgress(Long employeeId) {
        List<OnboardingTask> tasks = taskRepo.findByEmployeeId(employeeId);
        if (tasks.isEmpty()) return 0;
        long done = tasks.stream().filter(OnboardingTask::isCompleted).count();
        return (int) Math.round((done * 100.0) / tasks.size());
    }

    /* ============================================================
       FIXED: Better Principal → Employee ID (uses email from security context)
       ============================================================ */
    @Override
    public Long getEmployeeIdFromPrincipal(Principal principal) {
        if (principal == null) return null;
        String username = principal.getName(); // usually email in Spring Security
        return employeeRepo.findByEmail(username)
                .map(Employee::getId)
                .orElseThrow(() -> new IllegalStateException("Employee not found for principal: " + username));
    }

    private void updateFlowCompletionStatus(Long employeeId) {
        List<OnboardingTask> tasks = taskRepo.findByEmployeeId(employeeId);
        boolean allDone = tasks.isEmpty() || tasks.stream().allMatch(OnboardingTask::isCompleted);

        flowRepo.findByEmployeeId(employeeId).ifPresent(flow -> {
            flow.setCompleted(allDone);
            flow.setCompletedAt(allDone ? LocalDate.now() : null);
            flowRepo.save(flow);
        });
    }
}
