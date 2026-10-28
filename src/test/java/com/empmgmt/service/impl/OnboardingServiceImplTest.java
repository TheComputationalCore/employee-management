package com.empmgmt.service.impl;

import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.model.OnboardingTask;
import com.empmgmt.repository.OnboardingFlowRepository;
import com.empmgmt.repository.OnboardingTaskRepository;
import com.empmgmt.repository.OnboardingTemplateTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OnboardingServiceImplTest {

    @TempDir
    Path temporaryDirectory;

    private OnboardingTaskRepository taskRepository;
    private OnboardingServiceImpl service;

    @BeforeEach
    void setUp() {
        taskRepository = mock(OnboardingTaskRepository.class);
        service = new OnboardingServiceImpl(taskRepository, mock(OnboardingFlowRepository.class),
                mock(OnboardingTemplateTaskRepository.class));
        ReflectionTestUtils.setField(service, "uploadDirectory", temporaryDirectory.toString());
    }

    @Test
    void rejectsUnsupportedUploadTypeAsAValidationError() {
        MockMultipartFile file = new MockMultipartFile("file", "payload.exe", "application/octet-stream", new byte[]{1});

        assertThrows(IllegalArgumentException.class, () -> service.uploadFile(1L, file));
        verify(taskRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void refusesToServeFilesOutsideOnboardingStorage() {
        OnboardingTask task = new OnboardingTask();
        task.setId(1L);
        task.setFilePath(temporaryDirectory.resolve("outside.pdf").toString());
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(ResourceNotFoundException.class, () -> service.getDocumentPath(1L));
    }
}
