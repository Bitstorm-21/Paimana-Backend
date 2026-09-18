package com.example.demo.repositories;

import com.example.demo.model.RiskPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiskPredictionRepository extends JpaRepository<RiskPrediction, Long> {

    @Query("SELECT r FROM RiskPrediction r WHERE r.monthlyProgress.project.projectId = :projectId AND r.monthlyProgress.monthName = :monthName")
    Optional<RiskPrediction> findByProjectIdAndMonthName(
            @Param("projectId") Long projectId,
            @Param("monthName") String monthName
    );

    @Query("SELECT r FROM RiskPrediction r WHERE LOWER(TRIM(r.monthlyProgress.project.ProjectName)) = LOWER(TRIM(:projectName)) AND LOWER(TRIM(r.monthlyProgress.monthName)) = LOWER(TRIM(:monthName))")
    Optional<RiskPrediction> findByProjectNameAndMonthName(
            @Param("projectName") String projectName,
            @Param("monthName") String monthName
    );
}
