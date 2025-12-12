package com.empmgmt.service;

import com.empmgmt.model.Application;
import com.empmgmt.model.Job;
import com.empmgmt.util.ResumeParser;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ResumeParsingService {

    /* ============================================================================
       UNIVERSAL SKILL DICTIONARY — Covers ALL common job roles
       ============================================================================ */
    private static final List<String> UNIVERSAL_SKILLS = List.of(
            // --- Software / IT ---
            "Java", "Spring", "Spring Boot", "Hibernate", "MySQL", "React", "Angular",
            "JavaScript", "Node", "Python", "C++", "C", "HTML", "CSS", "AWS", "Docker",
            "Kubernetes", "Git", "REST", "API", "Microservices",

            // --- Robotics & Mechanical ---
            "ROS", "Kinematics", "CAD", "Solidworks", "Matlab", "Control Systems",
            "Mechatronics", "Embedded Systems", "Sensors", "Actuators",

            // --- Electrical / Electronics ---
            "Circuit Design", "PCB", "VLSI", "Power Systems", "PLC", "SCADA",

            // --- Civil ---
            "AutoCAD", "Revit", "Construction", "Estimation", "Surveying",

            // --- Marketing ---
            "SEO", "SEM", "Digital Marketing", "Copywriting",

            // --- HR / Admin ---
            "Recruitment", "Payroll", "Onboarding", "HRIS", "Talent Acquisition",

            // --- Data / AI ---
            "Machine Learning", "Deep Learning", "Data Analysis", "TensorFlow", "Pandas",

            // --- Finance ---
            "Accounting", "Bookkeeping", "Financial Analysis",

            // --- UI/UX ---
            "Figma", "UI Design", "UX Research"
    );

    /* ============================================================================
       MAIN ENTRY POINT
       ============================================================================ */
    public Application parseResume(Application app, Job job) {

        String text = extractResumeText(app);
        String clean = text.toLowerCase();

        // --- UNIVERSAL SKILLS ---
        List<String> allSkills = detectUniversalSkills(clean);

        // --- JOB-SPECIFIC REQUIRED SKILLS ---
        List<String> jobSkills = splitCsv(job.getRequiredSkills());

        // --- FILTER skills that match job ---
        List<String> matchedSkills = extractJobSpecificSkills(allSkills, jobSkills);
        app.setParsedSkills(String.join(", ", matchedSkills));

        // --- Education + Experience ---
        int exp = detectExperienceYears(clean);
        app.setExperienceYears(exp);

        String edu = detectEducation(clean);
        app.setEducation(edu);

        // --- Missing Skills ---
        List<String> missing = detectMissingSkills(matchedSkills, jobSkills);
        app.setMissingSkills(String.join(", ", missing));

        // --- Score ---
        int score = calculateAiScore(matchedSkills, jobSkills, exp);
        app.setAiScore(score);

        // --- Summary ---
        app.setAiSummary(generateSummary(app.getFullName(), matchedSkills, exp, edu));

        return app;
    }


    /* ============================================================================
       SAFE PDF TEXT EXTRACTION
       ============================================================================ */
    private String extractResumeText(Application app) {
        try {
            if (app.getResumePath() != null && !app.getResumePath().isBlank()) {
                return ResumeParser.extractText(app.getResumePath());
            }
        } catch (Exception e) {
            System.out.println("WARN: Resume read failed for App: " + app.getId());
        }
        return "";
    }


    /* ============================================================================
       UNIVERSAL SKILL EXTRACTION
       ============================================================================ */
    private List<String> detectUniversalSkills(String text) {
        List<String> found = new ArrayList<>();

        for (String s : UNIVERSAL_SKILLS) {
            if (text.contains(s.toLowerCase())) found.add(s);
        }
        return found;
    }


    /* ============================================================================
       JOB-SPECIFIC SKILL FILTERING
       ============================================================================ */
    private List<String> extractJobSpecificSkills(List<String> resumeSkills, List<String> jobSkills) {

        if (jobSkills.isEmpty()) return resumeSkills; // fallback

        List<String> matched = new ArrayList<>();

        for (String req : jobSkills) {
            for (String skill : resumeSkills) {
                if (skill.equalsIgnoreCase(req)) {
                    matched.add(skill);
                }
            }
        }
        return matched;
    }


    private List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();

        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }


    /* ============================================================================
       EXPERIENCE DETECTION
       ============================================================================ */
    private int detectExperienceYears(String text) {
        Pattern p = Pattern.compile("(\\d+)[+ ]*(years|year|yrs|yr)");
        Matcher m = p.matcher(text);
        if (m.find()) {
            try { return Integer.parseInt(m.group(1)); } catch (Exception ignored) {}
        }
        return 0;
    }


    /* ============================================================================
       EDUCATION DETECTION
       ============================================================================ */
    private String detectEducation(String text) {
        List<String> degrees = List.of(
                "btech", "mtech", "bachelor", "masters", "mba", "bsc",
                "msc", "phd", "diploma", "university"
        );

        for (String d : degrees) {
            if (text.contains(d.toLowerCase())) return capitalize(d);
        }
        return "Not Found";
    }


    /* ============================================================================
       MISSING SKILLS DETECTION
       ============================================================================ */
    private List<String> detectMissingSkills(List<String> foundSkills, List<String> requiredSkills) {
        Set<String> foundLower = foundSkills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<String> missing = new ArrayList<>();

        for (String req : requiredSkills) {
            if (!foundLower.contains(req.toLowerCase())) {
                missing.add(capitalize(req));
            }
        }
        return missing;
    }


    /* ============================================================================
       AI SCORE CALCULATION
       ============================================================================ */
    private int calculateAiScore(List<String> foundSkills, List<String> requiredSkills, int expYears) {

        int score = 0;

        // --- Skill Score (70) ---
        int matched = 0;
        for (String req : requiredSkills) {
            if (foundSkills.stream().anyMatch(s -> s.equalsIgnoreCase(req))) matched++;
        }
        if (!requiredSkills.isEmpty()) {
            score += (matched * 70) / requiredSkills.size();
        }

        // --- Experience Score (20) ---
        score += Math.min(expYears * 4, 20);

        // --- Resume Quality Constant (10) ---
        score += 10;

        return Math.min(score, 100);
    }


    /* ============================================================================
       SUMMARY
       ============================================================================ */
    private String generateSummary(String name, List<String> skills, int exp, String edu) {

        String skillText = skills.isEmpty()
                ? "relevant skills"
                : String.join(", ", skills);

        return name + " has " + exp + " years of experience and skills such as " +
                skillText + ". Education detected: " + edu + ".";
    }


    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
