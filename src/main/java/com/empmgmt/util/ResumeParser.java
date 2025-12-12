package com.empmgmt.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class ResumeParser {

    /* ------------------------------------------------------------------
     * 1. Extract text from PDF
     * ------------------------------------------------------------------ */
    public static String extractText(String filePath) {
        try (PDDocument document = Loader.loadPDF(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (Exception e) {
            log.error("Failed to parse PDF", e);
            return "";
        }
    }

    /* ------------------------------------------------------------------
     * 2. Skill extraction (simple keyword match)
     * ------------------------------------------------------------------ */
    public static Set<String> extractSkills(String text) {
        List<String> keywords = List.of(
                "java", "spring", "hibernate", "mysql",
                "javascript", "react", "html", "css",
                "aws", "docker", "kubernetes", "git",
                "python", "machine learning", "api"
        );

        Set<String> found = new HashSet<>();
        String lower = text.toLowerCase();

        for (String k : keywords) {
            if (lower.contains(k)) found.add(k);
        }

        return found;
    }

    /* ------------------------------------------------------------------
     * 3. Experience years extraction
     * ------------------------------------------------------------------ */
    public static int extractExperienceYears(String text) {
        Pattern p = Pattern.compile("(\\d+)\\s+(years|yrs)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);

        int max = 0;
        while (m.find()) {
            max = Math.max(max, Integer.parseInt(m.group(1)));
        }
        return max;
    }

    /* ------------------------------------------------------------------
     * 4. Education extraction
     * ------------------------------------------------------------------ */
    public static String extractEducation(String text) {
        String[] degrees = {
                "B.Tech", "BSc", "M.Tech", "MSc",
                "MBA", "PhD", "Bachelor", "Master"
        };

        String lower = text.toLowerCase();

        for (String d : degrees) {
            if (lower.contains(d.toLowerCase())) return d;
        }
        return "Not Found";
    }

    /* ------------------------------------------------------------------
     * 5. Missing skills detection
     * ------------------------------------------------------------------ */
    public static Set<String> detectMissingSkills(Set<String> resumeSkills, Set<String> requiredSkills) {
        if (requiredSkills == null) return Set.of();

        Set<String> missing = new HashSet<>(requiredSkills);
        missing.removeIf(req ->
                resumeSkills.stream().anyMatch(s -> s.equalsIgnoreCase(req))
        );
        return missing;
    }

    /* ------------------------------------------------------------------
     * 6. ✔ FIXED — Missing method for CareersController
     * ------------------------------------------------------------------ */
    public static Set<String> splitCsvSkills(String csv) {
        if (csv == null || csv.isBlank()) return Set.of();

        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}
