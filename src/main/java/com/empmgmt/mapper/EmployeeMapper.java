package com.empmgmt.mapper;

import com.empmgmt.dto.EmployeeDTO;
import com.empmgmt.model.Employee;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeDTO toDTO(Employee employee);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Employee toEntity(EmployeeDTO dto);
}
