package com.empmgmt.service.impl;

import com.empmgmt.model.Application;
import com.empmgmt.model.Job;
import com.empmgmt.repository.ApplicationRepository;
import com.empmgmt.repository.JobRepository;
import com.empmgmt.repository.CandidateScoreRepository;

import com.empmgmt.model.CandidateScore;
import com.empmgmt.service.ResumeParsingService;
import com.empmgmt.service.ScoringService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoringServiceImpl implements ScoringService {

    private final ApplicationRepository appRepo;
    private final JobRepository jobRepo;
    private final CandidateScoreRepository scoreRepo;
    private final ResumeParsingService resumeParsingService;

    /**
     * ============================================================
     * MAIN SCORING LOGIC
     * ============================================================
     */
    @Override
    public void scoreApplication(Long appId) {

        Application app = appRepo.findById(appId).orElseThrow();
        Job job = jobRepo.findById(app.getJobId()).orElseThrow();

        // Re-run resume parsing
        resumeParsingService.parseResume(app, job);

        appRepo.save(app);

        // Create or update candidate score
        CandidateScore score = scoreRepo.findByApplicationId(appId)
                .orElse(new CandidateScore());

        score.setApplicationId(appId);

        // 🎯 Correct Sub Scores
        int totalRequiredSkills = job.getRequiredSkills() == null ? 0 :
                job.getRequiredSkills().split(",").length;

        int matchedSkills = app.getParsedSkills() == null ? 0 :
                app.getParsedSkills().split(",").length;

        // Convert matched/required → 50-point scale
        int skillScore = (totalRequiredSkills == 0)
                ? 0
                : (matchedSkills * 50) / totalRequiredSkills;

        score.setSkillsScore(skillScore);

        // Experience: scale 0–30
        int expScore = Math.min(app.getExperienceYears() * 3, 30);
        score.setExperienceScore(expScore);

        // Resume quality fixed 20
        score.setResumeScore(20);

        // Final Score from AI Score
        score.setFinalScore(app.getAiScore());

        scoreRepo.save(score);
    }


    /**
     * ============================================================
     * AUTO SHORTLISTING
     * ============================================================
     */
    @Override
    public void autoShortlist(Long jobId) {

        List<Application> apps = appRepo.findByJobId(jobId);

        for (Application app : apps) {

            // Ensure scoring is up to date
            scoreApplication(app.getId());

            if (app.getAiScore() != null && app.getAiScore() >= 70) {
                app.setStatus("Shortlisted");
                appRepo.save(app);
            }
        }
    }
}
