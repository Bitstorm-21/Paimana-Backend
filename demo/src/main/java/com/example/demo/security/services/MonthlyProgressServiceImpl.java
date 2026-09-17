package com.example.demo.security.services;

import com.example.demo.exceptions.APIException;
import com.example.demo.model.MonthlyProgress;
import com.example.demo.model.Project;
import com.example.demo.payload.MonthlyProgressDTO;
import com.example.demo.payload.MonthlyProgressRequest;
import com.example.demo.payload.MonthlyProgressResponse;
import com.example.demo.repositories.MonthlyProgressRepository;
import com.example.demo.repositories.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class MonthlyProgressServiceImpl implements MonthlyProgressService {

    @Autowired
    private MonthlyProgressRepository monthlyProgressRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // =====================================================
    // CREATE MONTHLY PROGRESS
    // =====================================================

    @Override
    public MonthlyProgressDTO createMonthlyProgress(
            Long projectId,
            MonthlyProgressRequest dto) {

        // -----------------------------------------
        // 1. CHECK PROJECT
        // -----------------------------------------
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new APIException(
                        "Project with projectId " + projectId + " not found!"
                ));

        // -----------------------------------------
        // 2. CHECK DUPLICATE
        // -----------------------------------------
        if (monthlyProgressRepository.existsByProject_ProjectIdAndMonthName(
                projectId, dto.getMonthName())) {

            throw new APIException(
                    "Monthly progress already exists for project "
                            + projectId
                            + " and month "
                            + dto.getMonthName()
            );
        }

        // -----------------------------------------
        // 3. CREATE ENTITY
        // -----------------------------------------
        MonthlyProgress monthlyProgress = new MonthlyProgress();

        monthlyProgress.setProject(project);
        monthlyProgress.setMonthName(dto.getMonthName());
        monthlyProgress.setRevisedCost(dto.getRevisedCost());
        monthlyProgress.setCumulativeExpenditure(dto.getCumulativeExpenditure());
        monthlyProgress.setPhysicalProgress(dto.getPhysicalProgress());

        // -----------------------------------------
        // 4. FIND PREVIOUS MONTH
        // -----------------------------------------
        MonthlyProgress previousMonth = monthlyProgressRepository
                .findPreviousMonth(projectId, dto.getMonthName())
                .orElse(null);

        // -----------------------------------------
        // 5. CALCULATE ALL FEATURES
        // -----------------------------------------
        calculateFeatures(
                monthlyProgress,
                previousMonth,
                project
        );

        // -----------------------------------------
        // 6. SAVE TO DATABASE
        // -----------------------------------------
        MonthlyProgress saved = monthlyProgressRepository.save(monthlyProgress);

        // -----------------------------------------
        // 7. RETURN DTO
        // -----------------------------------------
        return convertToDTO(saved);
    }

    // =====================================================
    // CALCULATE FEATURES
    // =====================================================
    private void calculateFeatures(
            MonthlyProgress current,
            MonthlyProgress previous,
            Project project) {

        BigDecimal currentProgress = current.getPhysicalProgress() != null
                ? current.getPhysicalProgress()
                : BigDecimal.ZERO;

        BigDecimal currentExpenditure = current.getCumulativeExpenditure() != null
                ? current.getCumulativeExpenditure()
                : BigDecimal.ZERO;

        BigDecimal currentCost = current.getRevisedCost() != null
                ? current.getRevisedCost()
                : BigDecimal.ZERO;

        BigDecimal originalCost = (project != null && project.getOriginalCost() != null)
                ? project.getOriginalCost()
                : BigDecimal.ZERO;

        // 1. PROGRESS VELOCITY
        BigDecimal progressVelocity;
        if (previous == null || previous.getPhysicalProgress() == null) {
            progressVelocity = currentProgress;
        } else {
            progressVelocity = currentProgress.subtract(previous.getPhysicalProgress());
        }
        current.setProgressVelocity(progressVelocity);

        // 2. FINANCIAL BURN %
        BigDecimal financialBurnPct = BigDecimal.ZERO;
        if (currentCost.compareTo(BigDecimal.ZERO) > 0) {
            financialBurnPct = currentExpenditure
                    .divide(currentCost, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        current.setFinancialBurnPct(financialBurnPct);

        // 3. PROGRESS FINANCIAL DIVERGENCE
        BigDecimal divergence = financialBurnPct.subtract(currentProgress);
        current.setProgressFinancialDivergence(divergence);

        // 4. IS STAGNANT
        boolean isStagnant = progressVelocity.compareTo(BigDecimal.ZERO) <= 0;
        current.setIsStagnant(isStagnant);

        // 5. COST OVERRUN VALUE
        BigDecimal costOverrunValue = currentCost.subtract(originalCost);
        current.setCostOverrunValue(costOverrunValue);

        // 6. COST OVERRUN FLAG
        boolean costOverrunFlag = currentCost.compareTo(originalCost) > 0;
        current.setCostOverrunFlag(costOverrunFlag);

        // 7. COMPLETION STATUS
        boolean completionStatus = currentProgress.compareTo(BigDecimal.valueOf(100)) >= 0;
        current.setCompletionStatus(completionStatus);
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @Override
    public MonthlyProgressResponse getAllMonthlyProgress(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<MonthlyProgress> monthlyProgressPage = monthlyProgressRepository.findAll(pageDetails);
        List<MonthlyProgress> monthlyProgress = monthlyProgressPage.getContent();

        if (monthlyProgress.isEmpty()) {
            throw new APIException("No monthly progress created till now.");
        }

        List<MonthlyProgressDTO> monthlyProgressDTOS = monthlyProgress.stream()
                .map(this::convertToDTO)
                .toList();

        MonthlyProgressResponse response = new MonthlyProgressResponse();
        response.setContent(monthlyProgressDTOS);
        response.setPageNumber(monthlyProgressPage.getNumber());
        response.setPageSize(monthlyProgressPage.getSize());
        response.setTotalElements(monthlyProgressPage.getTotalElements());
        response.setTotalPages(monthlyProgressPage.getTotalPages());
        response.setLastPage(monthlyProgressPage.isLast());

        return response;
    }

    // =====================================================
    // GET BY PROJECT
    // =====================================================

    @Override
    public MonthlyProgressResponse getMonthlyProgressByProject(
            Long projectId,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder) {

        if (!projectRepository.existsById(projectId)) {
            throw new APIException("Project with projectId " + projectId + " not found!");
        }

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<MonthlyProgress> monthlyProgressPage =
                monthlyProgressRepository.findByProject_ProjectId(projectId, pageDetails);

        List<MonthlyProgress> monthlyProgress = monthlyProgressPage.getContent();

        if (monthlyProgress.isEmpty()) {
            throw new APIException("No monthly progress found for project " + projectId);
        }

        List<MonthlyProgressDTO> monthlyProgressDTOS = monthlyProgress.stream()
                .map(this::convertToDTO)
                .toList();

        MonthlyProgressResponse response = new MonthlyProgressResponse();
        response.setContent(monthlyProgressDTOS);
        response.setPageNumber(monthlyProgressPage.getNumber());
        response.setPageSize(monthlyProgressPage.getSize());
        response.setTotalElements(monthlyProgressPage.getTotalElements());
        response.setTotalPages(monthlyProgressPage.getTotalPages());
        response.setLastPage(monthlyProgressPage.isLast());

        return response;
    }

    // =====================================================
    // ENTITY → DTO
    // =====================================================

    private MonthlyProgressDTO convertToDTO(MonthlyProgress monthlyProgress) {
        MonthlyProgressDTO dto = new MonthlyProgressDTO();

        dto.setProjectId(monthlyProgress.getProject().getProjectId());
        dto.setMonthName(monthlyProgress.getMonthName());
        dto.setRevisedCost(monthlyProgress.getRevisedCost());
        dto.setCumulativeExpenditure(monthlyProgress.getCumulativeExpenditure());
        dto.setPhysicalProgress(monthlyProgress.getPhysicalProgress());
        dto.setProgressVelocity(monthlyProgress.getProgressVelocity());
        dto.setFinancialBurnPct(monthlyProgress.getFinancialBurnPct());
        dto.setProgressFinancialDivergence(monthlyProgress.getProgressFinancialDivergence());
        dto.setIsStagnant(monthlyProgress.getIsStagnant());
        dto.setCostOverrunValue(monthlyProgress.getCostOverrunValue());
        dto.setCostOverrunFlag(monthlyProgress.getCostOverrunFlag());
        dto.setCompletionStatus(monthlyProgress.getCompletionStatus());

        return dto;
    }
}