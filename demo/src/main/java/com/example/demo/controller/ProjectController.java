package com.example.demo.controller;

import com.example.demo.config.AppConstants;
import com.example.demo.payload.ProjectDTO;
import com.example.demo.payload.ProjectResponse;
import com.example.demo.payload.SectorDTO;
import com.example.demo.payload.SectorResponse;
import com.example.demo.repositories.ProjectRepository;
import com.example.demo.security.services.ProjectService;
import jakarta.validation.Valid;
import org.hibernate.annotations.Audited;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectController {

    @Autowired
    ProjectService projectService ;
    @PostMapping("/admin/create/project/sector/{id}")
    public ResponseEntity<ProjectDTO> createSector(@Valid @RequestBody ProjectDTO projectDTO , @PathVariable Integer id) {
        ProjectDTO projectDTO1 = projectService.createProject(projectDTO , id) ;
        return new ResponseEntity<>(projectDTO1, HttpStatus.OK);
    }
    @GetMapping("/public/projects")
    public ResponseEntity<ProjectResponse> getAllSectors(   @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                             @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                             @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_Projects_BY, required = false) String sortBy,
                                                             @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        ProjectResponse projectResponse = projectService.getAllProjects(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(projectResponse, HttpStatus.OK);
    }
    @DeleteMapping("/admin/delete/{id}/project")
    public ResponseEntity<ProjectDTO> deleteProject(@PathVariable Long id){
        ProjectDTO projectDTO = projectService.deleteProject(id) ;
        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }
    @PutMapping("/admin/update/{id}/project")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @RequestBody ProjectDTO projectDTO){
        ProjectDTO projectDTO1 = projectService.updateProject(id,projectDTO) ;
        return new ResponseEntity<>(projectDTO1, HttpStatus.OK);
    }
    @GetMapping("/public/projects/{sectorName}")
    public ResponseEntity<ProjectResponse> getAllProjectsBySector(   @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                     @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                     @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_Projects_BY, required = false) String sortBy,
                                                                     @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder,
                                                                     @PathVariable String sectorName) {
        ProjectResponse projectResponse = projectService.getAllProjectsBySector(pageNumber, pageSize, sortBy, sortOrder,sectorName) ;
        return new ResponseEntity<>(projectResponse, HttpStatus.OK);
    }
    }

