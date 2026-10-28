package com.empmgmt.controller;

import com.empmgmt.model.Application;
import com.empmgmt.model.Job;
import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.repository.ApplicationRepository;
import com.empmgmt.repository.JobRepository;
import com.empmgmt.service.ResumeParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/careers")
public class CareersController {

    private final JobRepository jobRepo;
    private final ApplicationRepository appRepo;
    private final ResumeParsingService resumeParsingService;

    @Value("${app.upload-dir:uploads}")
    private String uploadDirectory;

    /* ---------------------------------------------------
     * PUBLIC JOB LISTING PAGE
     * --------------------------------------------------- */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("jobs", jobRepo.findByActiveTrue());
        return "careers/list";
    }

    /* ---------------------------------------------------
     * JOB DETAILS
     * --------------------------------------------------- */
    @GetMapping("/job/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("job", jobRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found")));
        return "careers/details";
    }

    /* ---------------------------------------------------
     * APPLY PAGE
     * --------------------------------------------------- */
    @GetMapping("/apply/{jobId}")
    public String applyForm(@PathVariable Long jobId, Model model) {
        model.addAttribute("job", jobRepo.findById(jobId)
                .filter(job -> Boolean.TRUE.equals(job.getActive()))
                .orElseThrow(() -> new ResourceNotFoundException("Job not found")));
        return "careers/apply";
    }

    /* ---------------------------------------------------
     * SUBMIT APPLICATION + AI PROCESSING + SAVING
     * --------------------------------------------------- */
    @PostMapping("/apply/{jobId}")
    public String submitApplication(
            @PathVariable Long jobId,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam MultipartFile resume,
            Model model
    ) throws Exception {
        Job job = jobRepo.findById(jobId)
                .filter(candidateJob -> Boolean.TRUE.equals(candidateJob.getActive()))
                .orElse(null);
        if (job == null) {
            return "redirect:/careers?jobUnavailable";
        }

        if (name == null || name.isBlank() || name.length() > 160
                || email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") || email.length() > 254
                || phone == null || phone.isBlank() || phone.length() > 40) {
            return redisplayApplicationForm(job, name, email, phone, "Enter a valid name, email address, and phone number.", model);
        }
        if (resume == null || resume.isEmpty()) {
            return redisplayApplicationForm(job, name, email, phone, "Choose a PDF resume to upload.", model);
        }
        if (resume.getSize() > 5L * 1024 * 1024) {
            return redisplayApplicationForm(job, name, email, phone, "Resume must be 5 MB or smaller.", model);
        }

        String originalFilename = StringUtils.cleanPath(resume.getOriginalFilename() == null ? "" : resume.getOriginalFilename());
        if (originalFilename.isBlank() || originalFilename.contains("..")
                || originalFilename.contains("/") || originalFilename.contains("\\")
                || !originalFilename.toLowerCase().endsWith(".pdf")) {
            return redisplayApplicationForm(job, name, email, phone, "Only PDF resumes with a valid filename are accepted.", model);
        }

        Path uploadPath = Path.of(uploadDirectory).toAbsolutePath().normalize().resolve("resumes").normalize();
        Files.createDirectories(uploadPath);
        Path destination = uploadPath.resolve(UUID.randomUUID() + ".pdf").normalize();
        if (!destination.startsWith(uploadPath)) {
            throw new IllegalArgumentException("Invalid destination path");
        }

        try (InputStream input = resume.getInputStream()) {
            byte[] signature = input.readNBytes(5);
            if (!new String(signature, StandardCharsets.US_ASCII).equals("%PDF-")) {
                return redisplayApplicationForm(job, name, email, phone, "The selected file is not a valid PDF.", model);
            }
        }
        try (InputStream input = resume.getInputStream()) {
            Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
        }

        Application app = new Application();
        app.setJobId(jobId);
        app.setFullName(name);
        app.setEmail(email);
        app.setPhone(phone);
        app.setResumePath(destination.toString());

        app.setStatus("Applied");
        Application saved;
        try {
            resumeParsingService.parseResume(app, job);
            saved = appRepo.save(app);
        } catch (RuntimeException | Error failure) {
            Files.deleteIfExists(destination);
            throw failure;
        }

        /* --------------------------
           9. SEND TRACKING ID TO PAGE
        -------------------------- */
        model.addAttribute("trackingId", saved.getId());

        return "careers/submitted";
    }

    private String redisplayApplicationForm(Job job, String name, String email, String phone,
                                            String error, Model model) {
        model.addAttribute("job", job);
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("error", error);
        return "careers/apply";
    }
}
