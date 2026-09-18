package com.example.demo.repositories;

import com.example.demo.model.MonthlyProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyProgressRepository extends JpaRepository<MonthlyProgress, Long> {

    boolean existsByProject_ProjectIdAndMonthName(Long projectId, String monthName);

    Page<MonthlyProgress> findByProject_ProjectId(Long projectId, Pageable pageable);

    @Query("SELECT m FROM MonthlyProgress m WHERE m.project.projectId = :projectId AND m.monthName < :monthName ORDER BY m.monthName DESC")
    List<MonthlyProgress> findPreviousMonths(@Param("projectId") Long projectId, @Param("monthName") String monthName);

    default Optional<MonthlyProgress> findPreviousMonth(Long projectId, String monthName) {
        List<MonthlyProgress> previousMonths = findPreviousMonths(projectId, monthName);
        return previousMonths.isEmpty() ? Optional.empty() : Optional.of(previousMonths.get(0));
    }

    @Query("SELECT m FROM MonthlyProgress m WHERE m.project.projectId = :projectId AND LOWER(TRIM(m.monthName)) = LOWER(TRIM(:monthName))")
    Optional<MonthlyProgress> findByProjectIdAndMonth(
            @Param("projectId") Long projectId,
            @Param("monthName") String monthName
    );

    @Query("SELECT m FROM MonthlyProgress m WHERE LOWER(TRIM(m.project.ProjectName)) = LOWER(TRIM(:projectName)) AND LOWER(TRIM(m.monthName)) = LOWER(TRIM(:monthName))")
    Optional<MonthlyProgress> findByProjectNameAndMonth(
            @Param("projectName") String projectName,
            @Param("monthName") String monthName
    );

    @Query("SELECT m FROM MonthlyProgress m WHERE LOWER(TRIM(m.project.ProjectName)) = LOWER(TRIM(:projectName))")
    Page<MonthlyProgress> findByProjectName(
            @Param("projectName") String projectName,
            Pageable pageable
    );
}