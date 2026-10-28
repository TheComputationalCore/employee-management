package com.empmgmt.controller;

import com.empmgmt.model.Job;
import com.empmgmt.repository.ApplicationRepository;
import com.empmgmt.repository.JobRepository;
import com.empmgmt.service.ResumeParsingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CareersControllerTest {

    private JobRepository jobRepository;
    private ApplicationRepository applicationRepository;
    private CareersController controller;

    @BeforeEach
    void setUp() {
        jobRepository = mock(JobRepository.class);
        applicationRepository = mock(ApplicationRepository.class);
        controller = new CareersController(jobRepository, applicationRepository, mock(ResumeParsingService.class));
        ReflectionTestUtils.setField(controller, "uploadDirectory", "uploads");
    }

    @Test
    void redirectsWhenJobIsMissing() throws Exception {
        when(jobRepository.findById(404L)).thenReturn(Optional.empty());

        String view = controller.submitApplication(404L, "Applicant", "person@example.com", "1234567890",
                new MockMultipartFile("resume", "resume.pdf", "application/pdf", "%PDF-1.4".getBytes()),
                new ExtendedModelMap());

        assertEquals("redirect:/careers?jobUnavailable", view);
        verify(applicationRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void redisplaysFormForInvalidPdfWithoutSavingApplication() throws Exception {
        Job job = new Job();
        job.setId(7L);
        job.setActive(true);
        when(jobRepository.findById(7L)).thenReturn(Optional.of(job));
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.submitApplication(7L, "Applicant", "person@example.com", "1234567890",
                new MockMultipartFile("resume", "resume.pdf", "application/pdf", "not a PDF".getBytes()), model);

        assertEquals("careers/apply", view);
        assertTrue(model.containsAttribute("error"));
        assertEquals(job, model.getAttribute("job"));
        verify(applicationRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
