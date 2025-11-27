package com.example.employeemanagement.mapper;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.dto.EmployeeWebDTO;
import com.example.employeemanagement.model.Employee;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    /* -------------------------
       ENTITY → DTO
    ------------------------- */
    EmployeeDTO toDto(Employee employee);

    List<EmployeeDTO> toDtoList(List<Employee> employees);

    /* -------------------------
       DTO → ENTITY
    ------------------------- */
    Employee toEntity(EmployeeDTO dto);

    /* -------------------------
       ENTITY → WEB DTO
    ------------------------- */
    EmployeeWebDTO toWebDto(Employee employee);

    /* -------------------------
       WEB DTO → ENTITY
    ------------------------- */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromWebDto(EmployeeWebDTO dto, @MappingTarget Employee employee);

}
