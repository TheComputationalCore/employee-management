package com.empmgmt.controller;

import com.empmgmt.model.*;
import com.empmgmt.repository.CandidateScoreRepository;
import com.empmgmt.repository.OfferLetterRepository;
import com.empmgmt.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/recruitment")
public class RecruitmentController {

    private final JobService jobService;
    private final ApplicationService appService;
    private final InterviewService interviewService;
    private final ScoringService scoringService;
    private final NoteService noteService;
    private final OfferLetterService offerService;
    private final ResumeParsingService resumeParsingService;
    private final EmployeeCreationService employeeCreationService;
    private final OnboardingService onboardingService;

    private final CandidateScoreRepository scoreRepo;
    private final OfferLetterRepository offerRepo;

    // JOBS
    @GetMapping("/jobs")
    public String jobs(Model model) {
        model.addAttribute("jobs", jobService.getAll());
        return "recruitment/jobs";
    }

    @GetMapping("/jobs/add")
    public String addJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "recruitment/add-job";
    }

    @PostMapping("/jobs/add")
    public String addJob(Job job) {
        job.setActive(true);
        jobService.create(job);
        return "redirect:/web/recruitment/jobs?added";
    }

    @GetMapping("/jobs/edit/{id}")
    public String editJobForm(@PathVariable Long id, Model model) {
        model.addAttribute("job", jobService.getById(id));
        return "recruitment/edit-job";
    }

    @PostMapping("/jobs/edit/{id}")
    public String editJob(@PathVariable Long id, Job updated) {
        jobService.update(id, updated);
        return "redirect:/web/recruitment/jobs?updated";
    }

    @GetMapping("/jobs/close/{id}")
    public String closeJob(@PathVariable Long id) {
        jobService.close(id);
        return "redirect:/web/recruitment/jobs?closed";
    }

    @GetMapping("/jobs/delete/{id}")
    public String deleteJob(@PathVariable Long id) {
        jobService.delete(id);
        return "redirect:/web/recruitment/jobs?deleted";
    }

    // APPLICATIONS LIST
    @GetMapping("/applications/{jobId}")
    public String viewApplications(@PathVariable Long jobId, Model model, RedirectAttributes ra) {
        if (jobId == null || jobService.getById(jobId) == null) {
            ra.addFlashAttribute("error", "Job not found");
            return "redirect:/web/recruitment/jobs";
        }
        model.addAttribute("job", jobService.getById(jobId));
        model.addAttribute("apps", appService.getByJob(jobId));
        model.addAttribute("scores", scoreRepo.findAll());
        return "recruitment/applications";
    }

    // CANDIDATE PROFILE — 100% SAFE
    @GetMapping("/candidate/{appId}")
    public String candidateProfile(@PathVariable Long appId, Model model, RedirectAttributes ra) {
        if (appId == null) {
            ra.addFlashAttribute("error", "Invalid application");
            return "redirect:/web/recruitment/jobs";
        }

        Application app = appService.getById(appId);
        if (app == null) {
            ra.addFlashAttribute("error", "Application not found");
            return "redirect:/web/recruitment/jobs";
        }

        if (app.getJobId() == null) {
            ra.addFlashAttribute("error", "Application not linked to any job");
            return "redirect:/web/recruitment/jobs";
        }

        Job job = jobService.getById(app.getJobId());
        if (job == null) {
            ra.addFlashAttribute("error", "Job not found");
            return "redirect:/web/recruitment/jobs";
        }

        app = resumeParsingService.parseResume(app, job);
        appService.save(app);

        model.addAttribute("app", app);
        model.addAttribute("job", job);
        model.addAttribute("score", scoreRepo.findByApplicationId(appId).orElse(null));
        model.addAttribute("interviews", interviewService.getByApplication(appId));
        model.addAttribute("notes", noteService.getNotes(appId));
        model.addAttribute("offer", offerRepo.findByApplicationId(appId).orElse(null));

        String nextStep = switch (app.getStatus()) {
            case "Applied" -> "Shortlist or Reject";
            case "Shortlisted" -> "Schedule Interview";
            case "Interview Scheduled" -> "Conduct Interview";
            case "Interviewing" -> "Evaluate & Decide";
            case "Hired" -> "Prepare Offer Letter";
            case "Rejected" -> "No further action";
            default -> "Review application";
        };
        model.addAttribute("nextStep", nextStep);

        return "recruitment/candidate-profile";
    }

    // RECALCULATE SCORE
    @GetMapping("/candidate/{appId}/recalculate")
    public String recalculateScore(@PathVariable Long appId, RedirectAttributes ra) {
        if (appId == null) {
            ra.addFlashAttribute("error", "Invalid application");
            return "redirect:/web/recruitment/jobs";
        }
        try {
            Application app = appService.getById(appId);
            if (app == null || app.getJobId() == null) {
                ra.addFlashAttribute("error", "Application or job missing");
                return "redirect:/web/recruitment/jobs";
            }
            Job job = jobService.getById(app.getJobId());
            app = resumeParsingService.parseResume(app, job);
            appService.save(app);
            scoringService.scoreApplication(appId);
            ra.addFlashAttribute("success", "AI Score recalculated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed: " + e.getMessage());
        }
        return "redirect:/web/recruitment/candidate/" + appId;
    }

    // INITIAL SCORING
    @GetMapping("/score/{appId}/{jobId}")
    public String scoreOne(@PathVariable Long appId, @PathVariable Long jobId, RedirectAttributes ra) {
        if (appId == null || jobId == null) {
            ra.addFlashAttribute("error", "Invalid request");
            return "redirect:/web/recruitment/jobs";
        }
        scoringService.scoreApplication(appId);
        return "redirect:/web/recruitment/applications/" + jobId + "?scored";
    }

    @GetMapping("/smart-shortlist/{jobId}")
    public String smartShortlist(@PathVariable Long jobId) {
        scoringService.autoShortlist(jobId);
        return "redirect:/web/recruitment/applications/" + jobId + "?smartShortlist";
    }

    // STATUS UPDATE → HIRE
    @PostMapping("/applications/status/{id}")
    @Transactional
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        Application app = appService.getById(id);
        appService.updateStatus(id, status);
        if ("Hired".equals(status)) {
            Long employeeId = employeeCreationService.createFromApplication(app);
            onboardingService.startOnboarding(employeeId);
        }
        return "redirect:/web/recruitment/candidate/" + id + "?statusUpdated";
    }

    // NOTES
    @PostMapping("/candidate/{appId}/note")
    public String addNote(@PathVariable Long appId, @RequestParam String note, Authentication auth) {
        noteService.addNote(appId, note, auth.getName());
        return "redirect:/web/recruitment/candidate/" + appId + "?noteAdded";
    }

    // OFFER LETTER FORM
    @GetMapping("/offer/{appId}")
    public String offerForm(@PathVariable Long appId, Model model) {
        Application app = appService.getById(appId);
        Job job = jobService.getById(app.getJobId());
        model.addAttribute("app", app);
        model.addAttribute("job", job);
        return "recruitment/offer-form";
    }

    // GENERATE OFFER
    @PostMapping("/offer/{appId}")
    public String generateOffer(@PathVariable Long appId,
                                @RequestParam String position,
                                @RequestParam Double salary,
                                @RequestParam String joiningDate) {
        offerService.generateOffer(appId, position, salary, joiningDate);
        return "redirect:/web/recruitment/candidate/" + appId + "?offerGenerated";
    }

    // DOWNLOAD OFFER PDF — 100% WORKING
    @GetMapping("/offer/download/{appId}")
    public ResponseEntity<Resource> downloadOffer(@PathVariable Long appId) throws Exception {
        OfferLetter offer = offerRepo.findByApplicationId(appId)
                .orElseThrow(() -> new RuntimeException("Offer letter not found"));

        Path pdfPath = Path.of(offer.getFilePath());
        if (!Files.exists(pdfPath)) {
            throw new RuntimeException("PDF file not found: " + pdfPath);
        }

        Resource resource = new UrlResource(pdfPath.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"Offer_Letter_" + appId + "_" + LocalDate.now() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(Files.size(pdfPath))
                .body(resource);
    }

    // INTERVIEW SCHEDULING
    @GetMapping("/interview/schedule/{appId}")
    public String scheduleInterviewForm(@PathVariable Long appId, Model model, RedirectAttributes ra) {
        Application app = appService.getById(appId);
        if (app == null) {
            ra.addFlashAttribute("error", "Application not found");
            return "redirect:/web/recruitment/jobs";
        }
        model.addAttribute("app", app);
        return "recruitment/interview-schedule";
    }

    @PostMapping("/interview/schedule/{appId}")
    public String scheduleInterview(@PathVariable Long appId,
                                    @RequestParam String type,
                                    @RequestParam String date,
                                    @RequestParam String time,
                                    @RequestParam String location,
                                    @RequestParam(required = false) String notes) {
        interviewService.scheduleInterview(appId, type, date, time, location, notes);
        return "redirect:/web/recruitment/candidate/" + appId + "?interviewScheduled";
    }

    // RESUME DOWNLOAD
    @GetMapping("/resume/{appId}")
    public ResponseEntity<Resource> getResume(@PathVariable Long appId) throws Exception {
        Application app = appService.getById(appId);
        if (app.getResumePath() == null) throw new RuntimeException("Resume not found");
        Path filePath = Path.of(app.getResumePath());
        if (!Files.exists(filePath)) throw new RuntimeException("File not found");
        Resource resource = new UrlResource(filePath.toUri());
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "inline; filename=\"" + filePath.getFileName() + "\"")
                .body(resource);
    }
}