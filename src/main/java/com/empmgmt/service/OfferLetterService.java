package com.empmgmt.service;

import com.empmgmt.model.OfferLetter;

public interface OfferLetterService {

    OfferLetter generateOffer(Long applicationId, String position, Double salary, String joiningDate);
}
