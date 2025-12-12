package com.empmgmt.service.impl;

import com.empmgmt.model.CandidateNote;
import com.empmgmt.repository.CandidateNoteRepository;
import com.empmgmt.service.NoteService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final CandidateNoteRepository repo;

    @Override
    public void addNote(Long appId, String note, String createdBy) {

        CandidateNote n = CandidateNote.builder()
                .applicationId(appId)
                .note(note)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();

        repo.save(n);
    }

    @Override
    public List<CandidateNote> getNotes(Long appId) {
        return repo.findByApplicationIdOrderByCreatedAtDesc(appId);
    }
}
