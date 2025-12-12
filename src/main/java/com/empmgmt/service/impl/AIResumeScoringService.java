package com.empmgmt.service.impl;

import com.empmgmt.model.Application;
import com.empmgmt.model.Job;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIResumeScoringService {

    /* ============================================================
       SCORE WEIGHTS (Adjust anytime)
       ============================================================ */
    private static final int SKILL_WEIGHT = 60;       // 60% Importance
    private static final int EXPERIENCE_WEIGHT = 25;  // 25% Importance
    private static final int EDUCATION_WEIGHT = 10;   // 10% Importance
    private static final int KEYWORD_BONUS = 5;       // 5% Resume keywords bonus


    /* ============================================================
       MAIN SCORING ENTRY POINT
       ============================================================ */
    public int score(Application app, Job job) {

        Set<String> candidateSkills = splitSkills(app.getParsedSkills());
        Set<String> requiredSkills = splitSkills(job.getRequiredSkills());

        int experienceYears = app.getExperienceYears() != null ? app.getExperienceYears() : 0;
        String education = app.getEducation() != null ? app.getEducation() : "";


        /* -------------------------
           1. Skill Match Score
        ------------------------- */
        int skillScore = calculateSkillScore(candidateSkills, requiredSkills);


        /* -------------------------
           2. Experience Score
        ------------------------- */
        int experienceScore = calculateExperienceScore(experienceYears);


        /* -------------------------
           3. Education Score
        ------------------------- */
        int educationScore = calculateEducationScore(education);


        /* -------------------------
           4. Resume Keyword Bonus
        ------------------------- */
        int bonusScore = calculateBonus(app, job);


        /* -------------------------
           5. Final Score
        ------------------------- */
        int finalScore = skillScore + experienceScore + educationScore + bonusScore;

        return Math.min(finalScore, 100);
    }



    /* ============================================================
       SKILL SCORING (60%)
       ============================================================ */
    private int calculateSkillScore(Set<String> candidateSkills, Set<String> requiredSkills) {

        if (requiredSkills.isEmpty()) return 0;

        long matched = requiredSkills.stream()
                .filter(r -> candidateSkills.contains(r.toLowerCase()))
                .count();

        double ratio = (double) matched / requiredSkills.size();

        return (int) (ratio * SKILL_WEIGHT);
    }



    /* ============================================================
       EXPERIENCE SCORING (25%)
       ============================================================ */
    private int calculateExperienceScore(int years) {
        if (years <= 0) return 0;
        if (years >= 5) return EXPERIENCE_WEIGHT; // full points for 5+ years
        return (years * EXPERIENCE_WEIGHT) / 5;
    }



    /* ============================================================
       EDUCATION SCORING (10%)
       ============================================================ */
    private int calculateEducationScore(String education) {

        if (education == null) return 0;

        education = education.toLowerCase();

        if (education.contains("phd")) return 10;
        if (education.contains("master")) return 8;
        if (education.contains("bachelor") || education.contains("b.tech") || education.contains("bsc"))
            return 6;
        if (education.contains("diploma")) return 4;

        return 2;
    }



    /* ============================================================
       BONUS SCORING (5%)
       ============================================================ */
    private int calculateBonus(Application app, Job job) {

        String resume = (app.getExperience() == null ? "" : app.getExperience()).toLowerCase();
        String title = job.getTitle().toLowerCase();

        return resume.contains(title) ? KEYWORD_BONUS : 0;
    }


    /* ============================================================
       AI SUMMARY GENERATION
       ============================================================ */
    public String summarizeCandidate(
            String name,
            Set<String> skills,
            int experience,
            String education,
            int score
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append(name).append(" appears to be a ");
        sb.append(scoreCategory(score)).append(" match based on resume analysis.\n\n");

        /* Summary Section */
        sb.append("• Experience: ").append(experience).append(" years\n");
        sb.append("• Education: ").append(education).append("\n");

        sb.append("• Skills: ");
        sb.append(skills.isEmpty() ? "Not detected" : String.join(", ", skills));
        sb.append("\n\n");

        /* Fit evaluation */
        if (score >= 80) {
            sb.append("Overall, this candidate shows a strong fit for this role.");
        } else if (score >= 60) {
            sb.append("This candidate has a moderate fit with relevant skills.");
        } else {
            sb.append("The candidate appears to have limited alignment with the role.");
        }

        return sb.toString();
    }


    /* ============================================================
       SCORE LABELING
       ============================================================ */
    private String scoreCategory(int score) {

        if (score >= 85) return "High";
        if (score >= 60) return "Medium";
        return "Low";
    }


    /* ============================================================
       CSV UTILS
       ============================================================ */
    private Set<String> splitSkills(String csv) {

        if (csv == null || csv.isBlank()) return Set.of();

        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }
}
