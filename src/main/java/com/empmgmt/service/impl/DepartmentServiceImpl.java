package com.empmgmt.service.impl;

import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.model.Department;
import com.empmgmt.repository.DepartmentRepository;
import com.empmgmt.service.DepartmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repo;

    @Override
    public Department create(Department d) {
        if (repo.existsByName(d.getName())) {
            throw new IllegalArgumentException("Department already exists");
        }
        return repo.save(d);
    }

    @Override
    public Department update(Long id, Department d) {
        Department dep = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        if (!dep.getName().equals(d.getName()) && repo.existsByName(d.getName())) {
            throw new IllegalArgumentException("Department name already exists");
        }

        dep.setName(d.getName());
        dep.setDescription(d.getDescription());

        return repo.save(dep);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Department not found");
        }
        repo.deleteById(id);
    }

    @Override
    public Department getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }

    @Override
    public List<Department> getAll() {
        return repo.findAll();
    }
}
