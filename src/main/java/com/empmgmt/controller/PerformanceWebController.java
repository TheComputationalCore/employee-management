package com.empmgmt.controller;

import com.empmgmt.dto.PerformanceDTO;
import com.empmgmt.security.service.CustomUserDetails;
import com.empmgmt.service.EmployeeService;
import com.empmgmt.service.PerformanceService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/performance")
public class PerformanceWebController {

    private final PerformanceService service;
    private final EmployeeService employeeService;

    // ADMIN/HR – LIST ALL REVIEWS
    @GetMapping
    public String list(Model model) {
        model.addAttribute("reviews", service.getAllReviews());
        return "performance/list";
    }

    // EMPLOYEE – MY REVIEWS
    @GetMapping("/my")
    public String myReviews(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        Long employeeId = user.getEmployeeId();

        model.addAttribute("reviews", service.getReviewsForEmployee(employeeId));
        return "performance/my";
    }

    // ADMIN – CREATE CYCLE FORM
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "performance/create";
    }

    @PostMapping("/create")
    public String create(
            @RequestParam Long employeeId,
            @RequestParam String cycle
    ) {
        PerformanceDTO dto = PerformanceDTO.builder()
                .employeeId(employeeId)
                .cycle(cycle)
                .build();

        service.createReview(dto);

        return "redirect:/web/performance?created";
    }

    // EMPLOYEE – SELF REVIEW PAGE
    @GetMapping("/self/{id}")
    public String selfForm(@PathVariable Long id, Model model) {

        PerformanceDTO review = service.getById(id);

        // SECURITY: ensure employee owns this review
        Long loggedEmp = ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getEmployeeId();

        if (!review.getEmployeeId().equals(loggedEmp)) {
            return "redirect:/web/performance/my?forbidden";
        }

        model.addAttribute("review", review);
        return "performance/self-review";
    }

    // EMPLOYEE – SUBMIT SELF REVIEW
    @PostMapping("/self/{id}")
    public String submitSelfReview(
            @PathVariable Long id,
            @RequestParam Integer rating,
            @RequestParam String comments
    ) {

        PerformanceDTO review = service.getById(id);

        Long loggedEmp = ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getEmployeeId();

        if (!review.getEmployeeId().equals(loggedEmp)) {
            return "redirect:/web/performance/my?forbidden";
        }

        PerformanceDTO updated = service.submitSelfReview(id, rating, comments);

        return "redirect:/web/performance/my?selfSubmitted";
    }

    // ADMIN/HR – MANAGER REVIEW PAGE
    @GetMapping("/manager/{id}")
    public String managerForm(@PathVariable Long id, Model model) {

        model.addAttribute("review", service.getById(id));
        return "performance/review";
    }

    // ADMIN/HR – SUBMIT MANAGER REVIEW
    @PostMapping("/manager/{id}")
    public String submitManagerReview(
            @PathVariable Long id,
            @RequestParam Integer rating,
            @RequestParam String comments
    ) {
        service.managerReview(id, rating, comments);
        return "redirect:/web/performance?updated";
    }
}
