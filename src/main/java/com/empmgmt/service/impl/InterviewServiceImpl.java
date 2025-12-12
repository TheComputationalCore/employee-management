package com.empmgmt.service.impl;

import com.empmgmt.model.Application;
import com.empmgmt.model.Interview;
import com.empmgmt.repository.ApplicationRepository;
import com.empmgmt.repository.InterviewRepository;
import com.empmgmt.service.EmailService;
import com.empmgmt.service.InterviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepo;
    private final ApplicationRepository appRepo;
    private final EmailService emailService;

    /* ============================================================
       MAIN schedule(Interview interview)
       ============================================================ */
    @Override
    public Interview schedule(Interview interview) {

        if (interview.getDate() != null && interview.getTime() != null) {
            interview.setInterviewDateTime(
                    LocalDateTime.of(interview.getDate(), interview.getTime())
            );
        }

        Interview saved = interviewRepo.save(interview);

        Application app = appRepo.findById(interview.getApplicationId()).orElseThrow();
        app.setStatus("Interview Scheduled");
        app.setStatusUpdatedAt(LocalDate.now());
        appRepo.save(app);

        String emailBody = """
                Dear %s,

                Your interview has been scheduled.

                Interview Type: %s
                Date: %s
                Time: %s
                Location/Link: %s

                Regards,
                HR Team
                """.formatted(
                app.getFullName(),
                interview.getType(),
                interview.getDate(),
                interview.getTime(),
                interview.getLocationOrLink()
        );

        emailService.logEmail(app.getEmail(),
                "Interview Scheduled - " + app.getFullName(),
                emailBody);

        return saved;
    }

    /* ============================================================
       NEW — scheduleInterview(...) called by RecruitmentController
       ============================================================ */
    @Override
    public Interview scheduleInterview(Long appId,
                                       String type,
                                       String date,
                                       String time,
                                       String location,
                                       String notes) {

        Application app = appRepo.findById(appId).orElseThrow();

        Interview interview = Interview.builder()
                .applicationId(appId)
                .jobId(app.getJobId())
                .candidateName(app.getFullName())
                .type(type)
                .interviewType(type)
                .date(LocalDate.parse(date))
                .time(LocalTime.parse(time))
                .locationOrLink(location)
                .notes(notes)
                .build();

        return schedule(interview);
    }

    /* ============================================================
       GET ALL INTERVIEWS FOR AN APPLICATION
       ============================================================ */
    @Override
    public List<Interview> getByApplication(Long appId) {
        return interviewRepo.findByApplicationId(appId);
    }

    /* ============================================================
       CALENDAR EVENTS
       ============================================================ */
    @Override
    public List<Map<String, Object>> getCalendarEvents() {

        List<Interview> list = interviewRepo.findAll();
        List<Map<String, Object>> events = new ArrayList<>();

        for (Interview i : list) {

            Map<String, Object> e = new HashMap<>();

            e.put("title", i.getCandidateName() + " (" + i.getType() + ")");
            e.put("interviewId", i.getId());

            if (i.getDate() != null && i.getTime() != null) {
                e.put("start", i.getDate() + "T" + i.getTime());
            }

            e.put("candidate", i.getCandidateName());
            e.put("type", i.getType());
            e.put("location", i.getLocationOrLink());

            events.add(e);
        }

        return events;
    }
}
