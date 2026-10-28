package com.empmgmt.service.impl;

import com.empmgmt.dto.EmployeeSearchRequest;
import com.empmgmt.mapper.EmployeeMapper;
import com.empmgmt.model.EmployeeStatus;
import com.empmgmt.repository.EmployeeRepository;
import com.empmgmt.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeServiceImplTest {

    private EmployeeRepository repository;
    private EmployeeServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(EmployeeRepository.class);
        service = new EmployeeServiceImpl(
                repository,
                mock(EmployeeMapper.class),
                mock(UserRepository.class),
                mock(PasswordEncoder.class)
        );
        when(repository.findByStatus(any(), any(Pageable.class)))
                .thenAnswer(invocation -> new PageImpl<>(List.of(), invocation.getArgument(1), 0));
    }

    @Test
    void searchesRequestedInactiveStatus() {
        EmployeeSearchRequest request = new EmployeeSearchRequest();
        request.setStatus(EmployeeStatus.INACTIVE);

        service.searchEmployees(request);

        verify(repository).findByStatus(eq(EmployeeStatus.INACTIVE), any(Pageable.class));
    }

    @Test
    void appliesBoundedPaginationAndSafeSortFallback() {
        EmployeeSearchRequest request = new EmployeeSearchRequest();
        request.setPage(-2);
        request.setSize(5000);
        request.setSort("password,desc");

        service.searchEmployees(request);

        verify(repository).findByStatus(eq(EmployeeStatus.ACTIVE),
                eq(PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "id"))));
    }
}
