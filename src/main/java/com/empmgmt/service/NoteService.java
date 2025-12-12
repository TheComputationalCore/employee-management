package com.empmgmt.service;

import com.empmgmt.model.CandidateNote;

import java.util.List;

public interface NoteService {

    void addNote(Long appId, String note, String createdBy);

    List<CandidateNote> getNotes(Long appId);
}
