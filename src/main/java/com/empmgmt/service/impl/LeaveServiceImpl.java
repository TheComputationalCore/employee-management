package com.empmgmt.service.impl;

import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.model.LeaveRequest;
import com.empmgmt.model.LeaveStatus;
import com.empmgmt.repository.LeaveRepository;
import com.empmgmt.service.LeaveService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository repo;

    @Override
    public LeaveRequest applyLeave(LeaveRequest request) {
        if (request.getEmployeeId() == null || request.getType() == null
                || request.getStartDate() == null || request.getEndDate() == null
                || request.getReason() == null || request.getReason().isBlank()) {
            throw new IllegalArgumentException("Employee, leave type, dates, and reason are required");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Leave end date must be on or after its start date");
        }
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Leave start date cannot be in the past");
        }
        request.setStatus(LeaveStatus.PENDING);
        return repo.save(request);
    }

    @Override
    public List<LeaveRequest> getMyLeaves(Long employeeId) {
        return repo.findByEmployeeId(employeeId);
    }

    @Override
    public List<LeaveRequest> getAllLeaves() {
        return repo.findAll();
    }

    @Override
    public LeaveRequest approveLeave(Long id) {

        LeaveRequest r = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        requirePending(r);
        r.setStatus(LeaveStatus.APPROVED);
        return repo.save(r);
    }

    @Override
    public LeaveRequest rejectLeave(Long id) {

        LeaveRequest r = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        requirePending(r);
        r.setStatus(LeaveStatus.REJECTED);
        return repo.save(r);
    }

    private void requirePending(LeaveRequest request) {
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only pending leave requests can be reviewed");
        }
    }
}
