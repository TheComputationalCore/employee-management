package com.empmgmt.mapper;

import com.empmgmt.dto.EmployeeDTO;
import com.empmgmt.model.Employee;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-10T18:45:23+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public EmployeeDTO toDTO(Employee employee) {
        if ( employee == null ) {
            return null;
        }

        EmployeeDTO.EmployeeDTOBuilder employeeDTO = EmployeeDTO.builder();

        employeeDTO.id( employee.getId() );
        employeeDTO.firstName( employee.getFirstName() );
        employeeDTO.lastName( employee.getLastName() );
        employeeDTO.email( employee.getEmail() );
        employeeDTO.phone( employee.getPhone() );
        employeeDTO.department( employee.getDepartment() );
        employeeDTO.position( employee.getPosition() );
        employeeDTO.salary( employee.getSalary() );

        return employeeDTO.build();
    }

    @Override
    public Employee toEntity(EmployeeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Employee.EmployeeBuilder employee = Employee.builder();

        employee.id( dto.getId() );
        employee.firstName( dto.getFirstName() );
        employee.lastName( dto.getLastName() );
        employee.email( dto.getEmail() );
        employee.phone( dto.getPhone() );
        employee.department( dto.getDepartment() );
        employee.position( dto.getPosition() );
        employee.salary( dto.getSalary() );

        return employee.build();
    }
}
