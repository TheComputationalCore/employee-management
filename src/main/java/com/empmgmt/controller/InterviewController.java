package com.empmgmt.controller;

import com.empmgmt.model.Interview;
import com.empmgmt.service.InterviewService;
import com.empmgmt.service.ApplicationService;
import com.empmgmt.service.JobService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/recruitment/interviews")
public class InterviewController {

    private final InterviewService interviewService;
    private final ApplicationService appService;
    private final JobService jobService;

    @GetMapping("/schedule/{applicationId}")
    public String scheduleForm(@PathVariable Long applicationId, Model model) {

        model.addAttribute("application", appService.getById(applicationId));
        model.addAttribute("interview", new Interview());

        return "recruitment/schedule-interview";
    }

    @PostMapping("/schedule/{applicationId}")
    public String scheduleInterview(
            @PathVariable Long applicationId,
            Interview interview) {

        interview.setApplicationId(applicationId);

        Long jobId = appService.getById(applicationId).getJobId();
        interview.setJobId(jobId);

        interviewService.schedule(interview);

        return "redirect:/web/recruitment/applications/" + jobId + "?scheduled";
    }

    @GetMapping("/list/{applicationId}")
    public String interviewList(@PathVariable Long applicationId, Model model) {

        model.addAttribute("application", appService.getById(applicationId));
        model.addAttribute("interviews", interviewService.getByApplication(applicationId));

        return "recruitment/interview-list";
    }
}
