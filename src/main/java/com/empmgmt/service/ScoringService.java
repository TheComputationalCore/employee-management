package com.empmgmt.service;

public interface ScoringService {

    void scoreApplication(Long applicationId);

    void autoShortlist(Long jobId);
}
