package com.empmgmt.service;

import com.empmgmt.model.Interview;

import java.util.List;
import java.util.Map;

public interface InterviewService {

    // OLD
    Interview schedule(Interview interview);

    // NEW — required by RecruitmentController
    Interview scheduleInterview(Long appId,
                                String type,
                                String date,
                                String time,
                                String location,
                                String notes);

    List<Interview> getByApplication(Long appId);

    List<Map<String, Object>> getCalendarEvents();
}
