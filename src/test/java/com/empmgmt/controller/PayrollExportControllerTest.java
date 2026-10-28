package com.empmgmt.controller;

import com.empmgmt.model.Payroll;
import com.empmgmt.repository.PayrollRepository;
import com.empmgmt.service.EmployeeService;
import com.empmgmt.service.PayrollExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class PayrollExportControllerTest {

    @Test
    void employeeCanDownloadOwnPayslip() {
        Fixture fixture = new Fixture(17L);
        fixture.addPayroll(42L, 17L);

        fixture.controller.payslipPdf(42L, new MockHttpServletResponse(), employeeAuthentication());

        assertTrue(fixture.exported.get());
    }

    @Test
    void employeeCannotDownloadAnotherEmployeesPayslip() {
        Fixture fixture = new Fixture(17L);
        fixture.addPayroll(42L, 18L);

        assertThrows(AccessDeniedException.class, () -> fixture.controller.payslipPdf(
                42L, new MockHttpServletResponse(), employeeAuthentication()));
        assertFalse(fixture.exported.get());
    }

    @Test
    void employeeReceivesNotFoundForUnknownPayslip() {
        Fixture fixture = new Fixture(17L);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> fixture.controller.payslipPdf(
                        42L, new MockHttpServletResponse(), employeeAuthentication()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertFalse(fixture.exported.get());
    }

    private UsernamePasswordAuthenticationToken employeeAuthentication() {
        return new UsernamePasswordAuthenticationToken("employee", "n/a",
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
    }

    private static final class Fixture {
        private final Map<Long, Payroll> payrolls = new java.util.HashMap<>();
        private final AtomicBoolean exported = new AtomicBoolean();
        private final PayrollExportController controller;

        private Fixture(Long employeeId) {
            EmployeeService employeeService = (EmployeeService) Proxy.newProxyInstance(
                    EmployeeService.class.getClassLoader(), new Class<?>[]{EmployeeService.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("getEmployeeIdFromAuth")) return employeeId;
                        throw new UnsupportedOperationException(method.getName());
                    });

            PayrollRepository payrollRepository = (PayrollRepository) Proxy.newProxyInstance(
                    PayrollRepository.class.getClassLoader(), new Class<?>[]{PayrollRepository.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("findById")) {
                            return Optional.ofNullable(payrolls.get((Long) args[0]));
                        }
                        throw new UnsupportedOperationException(method.getName());
                    });

            PayrollExportService exportService = new PayrollExportService() {
                @Override public void exportAllPayrollsExcel(HttpServletResponse response) { }
                @Override public void exportPayrollPdf(Long payrollId, HttpServletResponse response) {
                    exported.set(true);
                }
                @Override public void exportEmployeePayrollPdf(Long employeeId, HttpServletResponse response) { }
            };

            controller = new PayrollExportController(exportService, payrollRepository, employeeService);
        }

        private void addPayroll(Long payrollId, Long employeeId) {
            payrolls.put(payrollId, Payroll.builder().id(payrollId).employeeId(employeeId).build());
        }
    }
}
