package com.empmgmt.repository;

import com.empmgmt.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobId(Long jobId);

    @Modifying
    @Query("UPDATE Application a SET a.status = :status WHERE a.id = :id")
    void updateStatusOnly(@Param("id") Long id, @Param("status") String status);

}
