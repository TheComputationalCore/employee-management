package com.empmgmt.repository;

import com.empmgmt.model.KPI;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KPIRepository extends JpaRepository<KPI, Long> {
}
