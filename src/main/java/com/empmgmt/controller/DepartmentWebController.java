package com.empmgmt.controller;

import com.empmgmt.model.Department;
import com.empmgmt.service.DepartmentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/departments")
public class DepartmentWebController {

    private final DepartmentService service;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("departments", service.getAll());
        return "departments/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("department", new Department());
        return "departments/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Department d) {
        service.create(d);
        return "redirect:/web/departments?success";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("department", service.getById(id));
        return "departments/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute Department d) {
        service.update(id, d);
        return "redirect:/web/departments?updated";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/web/departments?deleted";
    }
}
