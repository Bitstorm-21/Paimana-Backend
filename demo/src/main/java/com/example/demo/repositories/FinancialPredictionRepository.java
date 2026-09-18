package com.example.demo.repositories;

import com.example.demo.model.FinancialPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinancialPredictionRepository extends JpaRepository<FinancialPrediction, Long> {

    @Query("SELECT f FROM FinancialPrediction f WHERE f.monthlyProgress.project.projectId = :projectId AND f.monthlyProgress.monthName = :monthName")
    Optional<FinancialPrediction> findByProjectIdAndMonthName(
            @Param("projectId") Long projectId,
            @Param("monthName") String monthName
    );

    @Query("SELECT f FROM FinancialPrediction f WHERE LOWER(TRIM(f.monthlyProgress.project.ProjectName)) = LOWER(TRIM(:projectName)) AND LOWER(TRIM(f.monthlyProgress.monthName)) = LOWER(TRIM(:monthName))")
    Optional<FinancialPrediction> findByProjectNameAndMonthName(
            @Param("projectName") String projectName,
            @Param("monthName") String monthName
    );
}
