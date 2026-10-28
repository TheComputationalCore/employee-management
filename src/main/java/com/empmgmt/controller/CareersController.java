package com.empmgmt.controller;

import com.empmgmt.model.Application;
import com.empmgmt.model.Job;
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
        model.addAttribute("job", jobRepo.findById(id).orElseThrow());
        return "careers/details";
    }

    /* ---------------------------------------------------
     * APPLY PAGE
     * --------------------------------------------------- */
    @GetMapping("/apply/{jobId}")
    public String applyForm(@PathVariable Long jobId, Model model) {
        model.addAttribute("job", jobRepo.findById(jobId).orElseThrow());
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
                .orElseThrow(() -> new IllegalArgumentException("Job is not available"));

        if (resume == null || resume.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded");
        }
        if (resume.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException("Resume must be 5 MB or smaller");
        }

        String originalFilename = StringUtils.cleanPath(resume.getOriginalFilename() == null ? "" : resume.getOriginalFilename());
        if (originalFilename.isBlank() || originalFilename.contains("..")
                || originalFilename.contains("/") || originalFilename.contains("\\")
                || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF resumes with a valid filename are accepted");
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
                throw new IllegalArgumentException("Uploaded file is not a valid PDF");
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
}
