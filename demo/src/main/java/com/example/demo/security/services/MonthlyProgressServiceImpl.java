package com.example.demo.security.services;

import com.example.demo.exceptions.APIException;
import com.example.demo.model.FinancialPrediction;
import com.example.demo.model.Intervention;
import com.example.demo.model.MonthlyProgress;
import com.example.demo.model.Project;
import com.example.demo.model.RiskPrediction;
import com.example.demo.payload.FinancialPredictionDTO;
import com.example.demo.payload.InterventionDTO;
import com.example.demo.payload.MLPredictionRequest;
import com.example.demo.payload.MLPredictionResponse;
import com.example.demo.payload.MonthlyProgressDTO;
import com.example.demo.payload.MonthlyProgressRequest;
import com.example.demo.payload.MonthlyProgressResponse;
import com.example.demo.payload.RiskPredictionDTO;
import com.example.demo.repositories.FinancialPredictionRepository;
import com.example.demo.repositories.InterventionRepository;
import com.example.demo.repositories.MonthlyProgressRepository;
import com.example.demo.repositories.ProjectRepository;
import com.example.demo.repositories.RiskPredictionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MonthlyProgressServiceImpl implements MonthlyProgressService {

    @Autowired
    private MonthlyProgressRepository monthlyProgressRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MLServiceClient mlServiceClient;

    @Autowired
    private RiskPredictionRepository riskPredictionRepository;

    @Autowired
    private FinancialPredictionRepository financialPredictionRepository;

    @Autowired
    private InterventionRepository interventionRepository;

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
        // 7. GENERATE & PERSIST ML PREDICTIONS
        // -----------------------------------------
        generateAndSavePredictions(saved, project);

        // -----------------------------------------
        // 8. RETURN DTO
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

        if (sortBy == null || sortBy.isBlank() || "id.monthName".equalsIgnoreCase(sortBy) || "MonthName".equalsIgnoreCase(sortBy)) {
            sortBy = "monthName";
        }

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

        if (sortBy == null || sortBy.isBlank() || "id.monthName".equalsIgnoreCase(sortBy) || "MonthName".equalsIgnoreCase(sortBy)) {
            sortBy = "monthName";
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
    // GET BY PROJECT NAME AND MONTH
    // =====================================================

    @Override
    public MonthlyProgressDTO getMonthlyProgressByProjectNameAndMonth(
            String projectName,
            String monthName) {

        MonthlyProgress monthlyProgress = monthlyProgressRepository
                .findByProjectNameAndMonth(projectName, monthName)
                .orElseThrow(() -> new APIException(
                        "Monthly progress not found for project '" + projectName + "' and month '" + monthName + "'"
                ));

        return convertToDTO(monthlyProgress);
    }

    // =====================================================
    // GET ALL BY PROJECT NAME (SORTED DESCENDING BY DEFAULT)
    // =====================================================

    @Override
    public MonthlyProgressResponse getMonthlyProgressByProjectName(
            String projectName,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder) {

        if (sortBy == null || sortBy.isBlank() || "id.monthName".equalsIgnoreCase(sortBy) || "MonthName".equalsIgnoreCase(sortBy)) {
            sortBy = "monthName";
        }

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<MonthlyProgress> monthlyProgressPage =
                monthlyProgressRepository.findByProjectName(projectName, pageDetails);

        List<MonthlyProgress> monthlyProgress = monthlyProgressPage.getContent();

        if (monthlyProgress.isEmpty()) {
            throw new APIException("No monthly progress found for project '" + projectName + "'");
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

    // =====================================================
    // ML PREDICTION GENERATION & STORAGE
    // =====================================================

    @Transactional
    public void generateAndSavePredictions(MonthlyProgress monthlyProgress, Project project) {
        MLPredictionRequest mlRequest = MLPredictionRequest.builder()
                .physicalProgress(monthlyProgress.getPhysicalProgress() != null ? monthlyProgress.getPhysicalProgress().doubleValue() : 0.0)
                .financialBurnPct(monthlyProgress.getFinancialBurnPct() != null ? monthlyProgress.getFinancialBurnPct().doubleValue() : 0.0)
                .progressVelocity(monthlyProgress.getProgressVelocity() != null ? monthlyProgress.getProgressVelocity().doubleValue() : 0.0)
                .progressFinancialDivergence(monthlyProgress.getProgressFinancialDivergence() != null ? monthlyProgress.getProgressFinancialDivergence().doubleValue() : 0.0)
                .isStagnant(Boolean.TRUE.equals(monthlyProgress.getIsStagnant()) ? 1 : 0)
                .originalCost(project != null && project.getOriginalCost() != null ? project.getOriginalCost().doubleValue() : 0.0)
                .build();

        MLPredictionResponse mlResponse = mlServiceClient.predict(mlRequest);

        // 1. Risk Prediction
        RiskPrediction risk = new RiskPrediction();
        risk.setMonthlyProgress(monthlyProgress);
        risk.setRiskScore(mlResponse.getRiskScore() != null ? BigDecimal.valueOf(mlResponse.getRiskScore()) : BigDecimal.ZERO);
        risk.setRiskLevel(mlResponse.getRiskLevel());
        risk.setModelName("RandomForest+SHAP");
        risk.setShapDrivers(mlServiceClient.serializeShapDrivers(mlResponse.getShapDrivers()));
        risk.setPredictedAt(LocalDateTime.now());
        RiskPrediction savedRisk = riskPredictionRepository.save(risk);
        monthlyProgress.setRiskPrediction(savedRisk);

        // 2. Financial Prediction
        FinancialPrediction financial = new FinancialPrediction();
        financial.setMonthlyProgress(monthlyProgress);
        financial.setBestCaseOverrun(mlResponse.getBestCaseOverrun() != null ? BigDecimal.valueOf(mlResponse.getBestCaseOverrun()) : BigDecimal.ZERO);
        financial.setExpectedOverrun(mlResponse.getExpectedOverrun() != null ? BigDecimal.valueOf(mlResponse.getExpectedOverrun()) : BigDecimal.ZERO);
        financial.setWorstCaseOverrun(mlResponse.getWorstCaseOverrun() != null ? BigDecimal.valueOf(mlResponse.getWorstCaseOverrun()) : BigDecimal.ZERO);
        financial.setShockBuffer(mlResponse.getShockBuffer() != null ? BigDecimal.valueOf(mlResponse.getShockBuffer()) : BigDecimal.ZERO);
        financial.setPredictedAt(LocalDateTime.now());
        FinancialPrediction savedFinancial = financialPredictionRepository.save(financial);
        monthlyProgress.setFinancialPrediction(savedFinancial);

        // 3. Intervention
        Intervention intervention = new Intervention();
        intervention.setMonthlyProgress(monthlyProgress);
        intervention.setInterventionCode(mlResponse.getInterventionCode());
        intervention.setRecommendation(mlResponse.getInterventionRecommendation());
        intervention.setPriority(mlResponse.getInterventionPriority());
        intervention.setStatus("PENDING");
        intervention.setCreatedAt(LocalDateTime.now());
        Intervention savedIntervention = interventionRepository.save(intervention);
        monthlyProgress.setIntervention(savedIntervention);
    }
}