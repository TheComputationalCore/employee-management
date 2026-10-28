package com.empmgmt.service.impl;

import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.model.OnboardingFlow;
import com.empmgmt.model.OnboardingTask;
import com.empmgmt.model.OnboardingTemplateTask;
import com.empmgmt.repository.OnboardingFlowRepository;
import com.empmgmt.repository.OnboardingTaskRepository;
import com.empmgmt.repository.OnboardingTemplateTaskRepository;
import com.empmgmt.service.OnboardingService;
import com.empmgmt.security.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.security.Principal;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.UUID;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private final OnboardingTaskRepository taskRepo;
    private final OnboardingFlowRepository flowRepo;
    private final OnboardingTemplateTaskRepository templateRepo;

    @Value("${app.upload-dir:uploads}")
    private String uploadDirectory;

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
                    .flow(flow)
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
            if (file == null || file.isEmpty() || file.getSize() > 5L * 1024 * 1024) {
                throw new IllegalArgumentException("File must be non-empty and 5 MB or smaller");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()
                    || originalFilename.contains("..")
                    || originalFilename.contains("/") || originalFilename.contains("\\")) {
                throw new IllegalArgumentException("Invalid file name");
            }

            String lowerName = originalFilename.toLowerCase(Locale.ROOT);
            String extension = lowerName.substring(lowerName.lastIndexOf('.') + 1);
            if (!List.of("pdf", "doc", "docx", "txt").contains(extension)) {
                throw new IllegalArgumentException("Only PDF, DOC, DOCX, or TXT files are supported");
            }

            Path folder = Path.of(uploadDirectory).toAbsolutePath().normalize().resolve("onboarding").normalize();
            Files.createDirectories(folder);
            Path filePath = folder.resolve(UUID.randomUUID() + "." + extension).normalize();
            if (!filePath.startsWith(folder)) {
                throw new IllegalArgumentException("Invalid destination path");
            }
            Files.copy(file.getInputStream(), filePath);

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
        if (principal instanceof Authentication authentication
                && authentication.getPrincipal() instanceof CustomUserDetails user
                && user.getEmployeeId() != null) {
            return user.getEmployeeId();
        }
        throw new IllegalStateException("Authenticated user is not linked to an employee");
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
