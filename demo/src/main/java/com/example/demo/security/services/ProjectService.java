package com.example.demo.security.services;

import com.example.demo.payload.ProjectDTO;
import com.example.demo.payload.ProjectResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;

public interface ProjectService {
    ProjectDTO createProject( ProjectDTO projectDTO,Integer Id);
    ProjectResponse getAllProjects(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
ProjectDTO deleteProject(Long id);
ProjectDTO updateProject(Long id, ProjectDTO projectDTO);
ProjectResponse getAllProjectsBySector(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder,String sectorName);
}
