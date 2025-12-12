package com.empmgmt.service;

import com.empmgmt.model.Department;

import java.util.List;

public interface DepartmentService {

    Department create(Department d);

    Department update(Long id, Department d);

    void delete(Long id);

    Department getById(Long id);

    List<Department> getAll();
}
