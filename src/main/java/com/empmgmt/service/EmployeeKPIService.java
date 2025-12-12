package com.empmgmt.service;

import com.empmgmt.dto.EmployeeKPIDTO;
import java.util.List;

public interface EmployeeKPIService {

    EmployeeKPIDTO assignKPI(EmployeeKPIDTO dto);

    List<EmployeeKPIDTO> getEmployeeKPIs(Long employeeId);

    EmployeeKPIDTO submitSelfScore(Long id, Double achieved);

    EmployeeKPIDTO managerScore(Long id, Double achieved);
}
