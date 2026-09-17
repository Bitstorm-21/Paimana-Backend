package com.example.demo.repositories;

import com.example.demo.model.MonthlyProgress;
import com.example.demo.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {
@Query("SELECT Pi FROM Project Pi WHERE Pi.sector.sectorName = :sectorName")
Page<Project> findAllBySector(Pageable pageDetails, @Param("sectorName") String sectorName);
}
//@Query("SELECT Mpi FROM MonthlyProgress Mpi WHERE Mpi.project.projectId = ?1 AND Mpi.month = ?2")
//public MonthlyProgress exists(Long projectId, String monthName) ;