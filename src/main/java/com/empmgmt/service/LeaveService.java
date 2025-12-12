package com.empmgmt.service;

import com.empmgmt.model.LeaveRequest;

import java.util.List;

public interface LeaveService {

    LeaveRequest applyLeave(LeaveRequest request);

    List<LeaveRequest> getMyLeaves(Long employeeId);

    List<LeaveRequest> getAllLeaves();

    LeaveRequest approveLeave(Long id);

    LeaveRequest rejectLeave(Long id);
}
