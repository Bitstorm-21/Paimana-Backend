package com.example.demo.security.services;

import com.example.demo.exceptions.APIException;
import com.example.demo.model.Project;
import com.example.demo.model.Sector;
import com.example.demo.payload.ProjectDTO;
import com.example.demo.payload.ProjectResponse;
import com.example.demo.repositories.ProjectRepository;
import com.example.demo.repositories.SectorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
@Autowired
private ProjectRepository projectRepository;
@Autowired
private ModelMapper modelMapper;
@Autowired
private SectorRepository sectorRepository;

    @Override
    public ProjectDTO createProject(ProjectDTO projectDTO, Integer Id) {
        Project project = modelMapper.map(projectDTO,Project.class);
        Sector sector = sectorRepository.findById(Id)
                .orElseThrow(() -> new APIException(" Sector not found! !"));
        project.setSector(sector);
        projectRepository.save(project);
        return modelMapper.map(project,ProjectDTO.class);
    }//
    @Override
    public ProjectResponse getAllProjects(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder){
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Project> ProjectPage = projectRepository.findAll(pageDetails);

        List<Project> projects = ProjectPage.getContent();
        if (projects.isEmpty())
            throw new APIException("No Project created till now.");

        List<ProjectDTO> projectDTOS =projects.stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .toList();

        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setContent(projectDTOS);
        projectResponse.setPageNumber(ProjectPage.getNumber());
        projectResponse.setPageSize(ProjectPage.getSize());
        projectResponse.setTotalElements(ProjectPage.getTotalElements());
        projectResponse.setLastPage(ProjectPage.isLast());
        projectResponse.setTotalPages(ProjectPage.getTotalPages());
        return projectResponse ;
    }
@Override
    public ProjectDTO deleteProject(Long id) {
    Project project = projectRepository.findById(id)
            .orElseThrow(() -> new APIException(" Project with projectId not found !"));
        projectRepository.delete(project);
        return modelMapper.map(project,ProjectDTO.class);
}
@Override
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
    Project project = projectRepository.findById(id)
            .orElseThrow(() -> new APIException(" Project with projectId not found !"));
    project.setProjectName(projectDTO.getProjectName());
    project.setProjectDescription(projectDTO.getProjectDescription());
    project.setState(projectDTO.getState());
    //  "OriginalCost": 8500000000,
    //    "startTime": "2026-10-01",
    //    "OriginalEndTime": "2030-09-30"
    project.setOriginalCost(project.getOriginalCost());
    project.setOriginalEndTime(project.getOriginalEndTime());
    project.setStartTime(project.getStartTime());
    return modelMapper.map(projectRepository.save(project), ProjectDTO.class);
}
@Override
  public ProjectResponse getAllProjectsBySector(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder,String sectorName){
    Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
    System.out.println("Sector name :"+ sectorName);
    Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
    Page<Project> ProjectPage = projectRepository.findAllBySector(pageDetails,sectorName);

    List<Project> projects = ProjectPage.getContent();
    if (projects.isEmpty())
        throw new APIException("No Project created till now.");

    List<ProjectDTO> projectDTOS =projects.stream()
            .map(project -> modelMapper.map(project, ProjectDTO.class))
            .toList();

    ProjectResponse projectResponse = new ProjectResponse();
    projectResponse.setContent(projectDTOS);
    projectResponse.setPageNumber(ProjectPage.getNumber());
    projectResponse.setPageSize(ProjectPage.getSize());
    projectResponse.setTotalElements(ProjectPage.getTotalElements());
    projectResponse.setLastPage(ProjectPage.isLast());
    projectResponse.setTotalPages(ProjectPage.getTotalPages());
    return projectResponse ;
}

}
