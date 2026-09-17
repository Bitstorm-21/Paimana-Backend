package com.example.demo.repositories;

import com.example.demo.model.Project;
import com.example.demo.model.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, Integer> {


    Sector findBySectorName(String sectorName);
}
