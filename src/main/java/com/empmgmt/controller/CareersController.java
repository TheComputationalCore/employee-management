package com.empmgmt.controller;

import com.empmgmt.model.Application;
import com.empmgmt.model.Job;
import com.empmgmt.repository.ApplicationRepository;
import com.empmgmt.repository.JobRepository;
import com.empmgmt.service.impl.AIResumeScoringService;
import com.empmgmt.util.ResumeParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/careers")
public class CareersController {

    private final JobRepository jobRepo;
    private final ApplicationRepository appRepo;
    private final AIResumeScoringService aiScoringService;

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

        /* --------------------------
           1. SAVE RESUME TO DISK (SAFE)
        -------------------------- */
        String uploadDir = "uploads/resumes/";
        Files.createDirectories(Path.of(uploadDir));

        // Validate original filename presence
        String originalFilename = resume.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Invalid uploaded file: missing filename");
        }

        // Reject path traversal / separators
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename: path sequences are forbidden");
        }

        // Extract and sanitize extension
        String safeExtension = "";
        int extDot = originalFilename.lastIndexOf('.');
        if (extDot != -1 && extDot < originalFilename.length() - 1) {
            safeExtension = originalFilename.substring(extDot).replaceAll("[^\\.a-zA-Z0-9]", "");
        }

        // Optionally restrict allowed file extensions for greater safety
        // Adjust allowed set as required for your app
        String extLower = safeExtension.toLowerCase();
        if (!extLower.isEmpty()) {
            if (!extLower.equals(".pdf") && !extLower.equals(".doc") && !extLower.equals(".docx") && !extLower.equals(".txt")) {
                throw new IllegalArgumentException("Unsupported file type: " + safeExtension);
            }
        } else {
            // If no extension, optionally reject
            throw new IllegalArgumentException("Uploaded file must have an extension (e.g. .pdf, .docx)");
        }

        // Build a safe, unique stored filename — use UUID so attacker-controlled name isn't used directly
        String storedFilename = System.currentTimeMillis() + "-" + UUID.randomUUID() + safeExtension;

        // Use Path API to avoid string-concat path issues
        Path storedPath = Path.of(uploadDir).resolve(storedFilename);

        // Write file atomically (overwrites if exists; UUID prevents collisions)
        Files.write(storedPath, resume.getBytes());

        String filePath = storedPath.toString();


        /* --------------------------
           2. PARSE RESUME TEXT
        -------------------------- */
        String text = ResumeParser.extractText(filePath);

        Set<String> parsedSkills = ResumeParser.extractSkills(text);
        int expYears = ResumeParser.extractExperienceYears(text);
        String education = ResumeParser.extractEducation(text);


        /* --------------------------
           3. CREATE BASE APPLICATION
        -------------------------- */
        Application app = new Application();
        app.setJobId(jobId);
        app.setFullName(name);
        app.setEmail(email);
        app.setPhone(phone);
        app.setResumePath(filePath);

        app.setStatus("Applied");
        app.setCreatedAt(LocalDate.now());
        app.setStatusUpdatedAt(LocalDate.now());

        app.setParsedSkills(parsedSkills.stream().collect(Collectors.joining(", ")));
        app.setExperienceYears(expYears);
        app.setEducation(education);


        /* --------------------------
           4. JOB REQUIREMENTS
        -------------------------- */
        Job job = jobRepo.findById(jobId).orElseThrow();
        Set<String> requiredSkills = ResumeParser.splitCsvSkills(job.getRequiredSkills());


        /* --------------------------
           5. AI SCORING
        -------------------------- */
        int aiScore = aiScoringService.score(app, job);
        app.setAiScore(aiScore);


        /* --------------------------
           6. MISSING SKILLS DETECTION
        -------------------------- */
        Set<String> missing = ResumeParser.detectMissingSkills(parsedSkills, requiredSkills);
        app.setMissingSkills(String.join(", ", missing));


        /* --------------------------
           7. AI SUMMARY GENERATION
        -------------------------- */
        String summary = aiScoringService.summarizeCandidate(
                name, parsedSkills, expYears, education, aiScore
        );
        app.setAiSummary(summary);


        /* --------------------------
           8. SAVE APPLICATION
        -------------------------- */
        Application saved = appRepo.save(app);


        /* --------------------------
           9. SEND TRACKING ID TO PAGE
        -------------------------- */
        model.addAttribute("trackingId", saved.getId());

        return "careers/submitted";
    }
}
