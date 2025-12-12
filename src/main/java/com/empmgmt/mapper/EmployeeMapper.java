package com.empmgmt.mapper;

import com.empmgmt.dto.EmployeeDTO;
import com.empmgmt.model.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeDTO toDTO(Employee employee);

    Employee toEntity(EmployeeDTO dto);
}
