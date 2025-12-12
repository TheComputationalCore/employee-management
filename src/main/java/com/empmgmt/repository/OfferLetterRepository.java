package com.empmgmt.repository;

import com.empmgmt.model.OfferLetter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfferLetterRepository extends JpaRepository<OfferLetter, Long> {

    Optional<OfferLetter> findByApplicationId(Long appId);
}
