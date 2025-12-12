package com.empmgmt.service.impl;

import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.model.LeaveRequest;
import com.empmgmt.model.LeaveStatus;
import com.empmgmt.repository.LeaveRepository;
import com.empmgmt.service.LeaveService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository repo;

    @Override
    public LeaveRequest applyLeave(LeaveRequest request) {
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

        r.setStatus(LeaveStatus.APPROVED);
        return repo.save(r);
    }

    @Override
    public LeaveRequest rejectLeave(Long id) {

        LeaveRequest r = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        r.setStatus(LeaveStatus.REJECTED);
        return repo.save(r);
    }
}
