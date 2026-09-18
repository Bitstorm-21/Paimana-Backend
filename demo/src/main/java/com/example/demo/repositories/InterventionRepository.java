package com.example.demo.repositories;

import com.example.demo.model.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    @Query("SELECT i FROM Intervention i WHERE i.monthlyProgress.project.projectId = :projectId AND i.monthlyProgress.monthName = :monthName")
    Optional<Intervention> findByProjectIdAndMonthName(
            @Param("projectId") Long projectId,
            @Param("monthName") String monthName
    );

    @Query("SELECT i FROM Intervention i WHERE LOWER(TRIM(i.monthlyProgress.project.ProjectName)) = LOWER(TRIM(:projectName)) AND LOWER(TRIM(i.monthlyProgress.monthName)) = LOWER(TRIM(:monthName))")
    Optional<Intervention> findByProjectNameAndMonthName(
            @Param("projectName") String projectName,
            @Param("monthName") String monthName
    );
}
