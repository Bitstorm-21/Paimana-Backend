package com.example.demo.repositories;

import com.example.demo.model.MonthlyProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthlyProgressRepository extends JpaRepository<MonthlyProgress, Long> {

    // Must be AndMonthName (not AndMonth) to match the property name
    boolean existsByProject_ProjectIdAndMonthName(Long projectId, String monthName);

    Page<MonthlyProgress> findByProject_ProjectId(Long projectId, Pageable pageable);

    @Query("SELECT m FROM MonthlyProgress m WHERE m.project.projectId = :projectId AND m.MonthName < :monthName ORDER BY m.MonthName DESC")
    Optional<MonthlyProgress> findPreviousMonth(@Param("projectId") Long projectId, @Param("monthName") String monthName);
}