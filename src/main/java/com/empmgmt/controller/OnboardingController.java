package com.empmgmt.controller;

import com.empmgmt.model.*;
import com.empmgmt.repository.OnboardingFlowRepository;
import com.empmgmt.service.EmployeeService;
import com.empmgmt.service.OnboardingService;
import com.empmgmt.service.OnboardingTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/onboarding")
public class OnboardingController {

    private final OnboardingService onboardingService;
    private final OnboardingTemplateService templateService;
    private final EmployeeService employeeService;
    private final OnboardingFlowRepository flowRepo;

    // HR DASHBOARD
    @GetMapping
    public String dashboard(Model model) {
        List<OnboardingFlow> flows = onboardingService.getAllFlows();

        long active = flows.stream().filter(f -> !f.isCompleted()).count();
        long completed = flows.stream().filter(OnboardingFlow::isCompleted).count();
        long pending = flows.stream()
                .flatMap(f -> onboardingService.getTasks(f.getEmployeeId()).stream())
                .filter(t -> !t.isCompleted())
                .count();
        long overdue = flows.stream()
                .flatMap(f -> onboardingService.getTasks(f.getEmployeeId()).stream())
                .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(LocalDate.now()) && !t.isCompleted())
                .count();

        model.addAttribute("stats", new DashboardStats(active, completed, overdue, pending));

        List<OnboardingDashboardDTO> onboardings = flows.stream()
                .map(f -> {
                    var emp = employeeService.getEmployee(f.getEmployeeId());
                    var tasks = onboardingService.getTasks(f.getEmployeeId());
                    int progress = onboardingService.getProgress(f.getEmployeeId());
                    boolean anyOverdue = tasks.stream()
                            .anyMatch(t -> t.getDueDate() != null && t.getDueDate().isBefore(LocalDate.now()) && !t.isCompleted());

                    return new OnboardingDashboardDTO(
                            f.getId(),
                            f.getEmployeeId(),
                            emp.getFirstName() + " " + emp.getLastName(),
                            emp.getEmail(),
                            emp.getDepartment(),
                            emp.getPosition(),
                            progress == 100,
                            anyOverdue,
                            progress
                    );
                })
                .toList();

        model.addAttribute("onboardings", onboardings);
        return "onboarding/dashboard";
    }

    // ONBOARDING DETAIL (HR VIEW)
    @GetMapping("/detail/{employeeId}")
    public String detail(@PathVariable Long employeeId, Model model) {
        var employee = employeeService.getEmployee(employeeId);
        model.addAttribute("employee", employee);
        model.addAttribute("tasks", onboardingService.getTasks(employeeId));
        model.addAttribute("progress", onboardingService.getProgress(employeeId));
        return "onboarding/detail";
    }

    // MY ONBOARDING (EMPLOYEE VIEW)
    @GetMapping("/my")
    public String myOnboarding(Model model, Principal principal) {
        Long employeeId = onboardingService.getEmployeeIdFromPrincipal(principal);
        model.addAttribute("employee", employeeService.getEmployee(employeeId));
        model.addAttribute("tasks", onboardingService.getTasks(employeeId));
        model.addAttribute("progress", onboardingService.getProgress(employeeId));
        return "onboarding/my-onboarding";
    }

    // FILE UPLOAD (EMPLOYEE)
    @PostMapping("/task/upload/{taskId}")
    public String uploadFile(@PathVariable Long taskId,
                             @RequestParam MultipartFile file,
                             Principal principal,
                             RedirectAttributes ra) {
        onboardingService.uploadFile(taskId, file);
        ra.addFlashAttribute("success", "Document uploaded successfully!");
        return "redirect:/web/onboarding/my?uploaded";
    }

    // MARK TASK AS COMPLETE - WORKS FOR BOTH HR AND EMPLOYEE
    @PostMapping("/task/complete/{taskId}")
    public String completeTask(@PathVariable Long taskId,
                               Principal principal,
                               RedirectAttributes ra) {

        Long employeeId = onboardingService.completeTask(taskId);  // this returns employeeId
        ra.addFlashAttribute("success", "Task marked as completed!");

        // Decide where to redirect based on who triggered it
        if (principal == null) {
            // Called from HR side (no Principal = likely admin clicking from detail page)
            return "redirect:/web/onboarding/detail/" + employeeId;
        } else {
            // Employee marking their own task
            return "redirect:/web/onboarding/my";
        }
    }

    // ADD TASK (HR)
    @GetMapping("/task/add/{employeeId}")
    public String addTaskForm(@PathVariable Long employeeId, Model model) {
        OnboardingTask task = new OnboardingTask();
        task.setEmployeeId(employeeId);
        model.addAttribute("task", task);
        model.addAttribute("employeeId", employeeId);
        return "onboarding/add-task";
    }

    @PostMapping("/task/add")
    public String addTask(@ModelAttribute OnboardingTask task, RedirectAttributes ra) {
        onboardingService.addTask(task);
        ra.addFlashAttribute("success", "Task added successfully!");
        return "redirect:/web/onboarding/detail/" + task.getEmployeeId();
    }

    // EDIT TASK
    @GetMapping("/task/edit/{taskId}")
    public String editTaskForm(@PathVariable Long taskId, Model model) {
        model.addAttribute("task", onboardingService.getTask(taskId));
        return "onboarding/edit-task";
    }

    @PostMapping("/task/edit/{taskId}")
    public String editTask(@PathVariable Long taskId,
                           @ModelAttribute OnboardingTask updated,
                           RedirectAttributes ra) {
        Long employeeId = onboardingService.updateTask(taskId, updated);
        ra.addFlashAttribute("success", "Task updated successfully!");
        return "redirect:/web/onboarding/detail/" + employeeId;
    }

    // DELETE TASK
    @GetMapping("/task/delete/{taskId}")
    public String deleteTask(@PathVariable Long taskId, RedirectAttributes ra) {
        Long employeeId = onboardingService.deleteTask(taskId);
        ra.addFlashAttribute("success", "Task deleted successfully!");
        return "redirect:/web/onboarding/detail/" + employeeId;
    }

    // TEMPLATES
    @GetMapping("/templates")
    public String templates(Model model) {
        model.addAttribute("templates", templateService.getTemplates());
        return "onboarding/templates";
    }

    @PostMapping("/templates/add")
    public String addTemplate(OnboardingTask formTask, RedirectAttributes ra) {
        var template = new com.empmgmt.model.OnboardingTemplateTask();
        template.setTitle(formTask.getTitle());
        template.setDescription(formTask.getDescription());
        template.setRequired(true);
        templateService.create(template);
        ra.addFlashAttribute("success", "Template added!");
        return "redirect:/web/onboarding/templates";
    }

    @GetMapping("/templates/delete/{id}")
    public String deleteTemplate(@PathVariable Long id, RedirectAttributes ra) {
        templateService.delete(id);
        ra.addFlashAttribute("success", "Template deleted!");
        return "redirect:/web/onboarding/templates";
    }

    // RESET ONBOARDING
    @GetMapping("/reset/{employeeId}")
    public String reset(@PathVariable Long employeeId, RedirectAttributes ra) {
        onboardingService.getTasks(employeeId)
                .forEach(t -> onboardingService.deleteTask(t.getId()));
        OnboardingFlow flow = flowRepo.findByEmployeeId(employeeId).orElseThrow();
        flowRepo.delete(flow);
        onboardingService.startOnboarding(employeeId);
        ra.addFlashAttribute("success", "Onboarding reset and restarted!");
        return "redirect:/web/onboarding";
    }

    // DTOs
    record DashboardStats(long active, long completed, long overdue, long pending) {}
    record OnboardingDashboardDTO(
            Long id, Long employeeId, String employeeName, String employeeEmail,
            String department, String position, boolean completed,
            boolean overdue, int progress) {}
}