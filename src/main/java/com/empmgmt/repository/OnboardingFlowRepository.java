package com.empmgmt.repository;

import com.empmgmt.model.OnboardingFlow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnboardingFlowRepository extends JpaRepository<OnboardingFlow, Long> {

    Optional<OnboardingFlow> findByEmployeeId(Long employeeId);
}
