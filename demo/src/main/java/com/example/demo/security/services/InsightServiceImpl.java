package com.example.demo.security.services;

import com.example.demo.exceptions.APIException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.model.FinancialPrediction;
import com.example.demo.model.Intervention;
import com.example.demo.model.MonthlyProgress;
import com.example.demo.model.RiskPrediction;
import com.example.demo.payload.*;
import com.example.demo.repositories.FinancialPredictionRepository;
import com.example.demo.repositories.InterventionRepository;
import com.example.demo.repositories.MonthlyProgressRepository;
import com.example.demo.repositories.RiskPredictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InsightServiceImpl implements InsightService {

    @Autowired
    private RiskPredictionRepository riskPredictionRepository;

    @Autowired
    private FinancialPredictionRepository financialPredictionRepository;

    @Autowired
    private InterventionRepository interventionRepository;

    @Autowired
    private MonthlyProgressRepository monthlyProgressRepository;

    @Autowired
    private MonthlyProgressService monthlyProgressService;

    @Autowired
    private MLServiceClient mlServiceClient;

    @Override
    public RiskPredictionDTO getRiskPrediction(Long projectId, String monthName) {
        String cleanMonth = monthName != null ? monthName.trim() : null;
        MonthlyProgress progress = monthlyProgressRepository.findByProjectIdAndMonth(projectId, cleanMonth)
                .orElseThrow(() -> new APIException("Monthly progress not found for projectId " + projectId + " and month " + monthName));

        ensurePredictions(progress);
        return convertRiskToDTO(progress.getRiskPrediction());
    }

    @Override
    public FinancialPredictionDTO getFinancialPrediction(Long projectId, String monthName) {
        String cleanMonth = monthName != null ? monthName.trim() : null;
        MonthlyProgress progress = monthlyProgressRepository.findByProjectIdAndMonth(projectId, cleanMonth)
                .orElseThrow(() -> new APIException("Monthly progress not found for projectId " + projectId + " and month " + monthName));

        ensurePredictions(progress);
        return convertFinancialToDTO(progress.getFinancialPrediction());
    }

    @Override
    public InterventionDTO getIntervention(Long projectId, String monthName) {
        String cleanMonth = monthName != null ? monthName.trim() : null;
        MonthlyProgress progress = monthlyProgressRepository.findByProjectIdAndMonth(projectId, cleanMonth)
                .orElseThrow(() -> new APIException("Monthly progress not found for projectId " + projectId + " and month " + monthName));

        ensurePredictions(progress);
        return convertInterventionToDTO(progress.getIntervention());
    }

    @Override
    public MonthlyInsightResponse getMonthlyInsights(Long projectId, String monthName) {
        String cleanMonth = monthName != null ? monthName.trim() : null;
        MonthlyProgress progress = monthlyProgressRepository.findByProjectIdAndMonth(projectId, cleanMonth)
                .orElseThrow(() -> new APIException(
                        "Monthly progress not found for projectId " + projectId + " and month " + monthName
                ));

        ensurePredictions(progress);

        String projectName = progress.getProject() != null ? progress.getProject().getProjectName() : null;

        return MonthlyInsightResponse.builder()
                .projectId(projectId)
                .projectName(projectName)
                .monthName(cleanMonth)
                .progress(convertProgressToDTO(progress))
                .riskPrediction(convertRiskToDTO(progress.getRiskPrediction()))
                .financialPrediction(convertFinancialToDTO(progress.getFinancialPrediction()))
                .intervention(convertInterventionToDTO(progress.getIntervention()))
                .build();
    }

    @Override
    public MonthlyInsightResponse getMonthlyInsightsByProjectName(String projectName, String monthName) {
        String cleanName = projectName != null ? projectName.trim() : null;
        String cleanMonth = monthName != null ? monthName.trim() : null;

        MonthlyProgress progress = monthlyProgressRepository.findByProjectNameAndMonth(cleanName, cleanMonth)
                .orElseThrow(() -> new APIException(
                        "Monthly progress not found for project '" + projectName + "' and month '" + monthName + "'"
                ));

        ensurePredictions(progress);

        Long projectId = progress.getProject() != null ? progress.getProject().getProjectId() : null;

        return MonthlyInsightResponse.builder()
                .projectId(projectId)
                .projectName(progress.getProject() != null ? progress.getProject().getProjectName() : cleanName)
                .monthName(cleanMonth)
                .progress(convertProgressToDTO(progress))
                .riskPrediction(convertRiskToDTO(progress.getRiskPrediction()))
                .financialPrediction(convertFinancialToDTO(progress.getFinancialPrediction()))
                .intervention(convertInterventionToDTO(progress.getIntervention()))
                .build();
    }

    private void ensurePredictions(MonthlyProgress progress) {
        if (progress.getRiskPrediction() == null || progress.getFinancialPrediction() == null || progress.getIntervention() == null) {
            monthlyProgressService.generateAndSavePredictions(progress, progress.getProject());
        }
    }

    private RiskPredictionDTO convertRiskToDTO(RiskPrediction r) {
        if (r == null) return null;
        RiskPredictionDTO dto = new RiskPredictionDTO();
        dto.setId(r.getId());
        if (r.getMonthlyProgress() != null && r.getMonthlyProgress().getProject() != null) {
            dto.setProjectId(r.getMonthlyProgress().getProject().getProjectId());
            dto.setMonthName(r.getMonthlyProgress().getMonthName());
        }
        dto.setRiskScore(r.getRiskScore());
        dto.setRiskLevel(r.getRiskLevel());
        dto.setModelName(r.getModelName());
        dto.setShapDrivers(mlServiceClient.deserializeShapDrivers(r.getShapDrivers()));
        dto.setPredictedAt(r.getPredictedAt());
        return dto;
    }

    private FinancialPredictionDTO convertFinancialToDTO(FinancialPrediction f) {
        if (f == null) return null;
        FinancialPredictionDTO dto = new FinancialPredictionDTO();
        dto.setId(f.getId());
        if (f.getMonthlyProgress() != null && f.getMonthlyProgress().getProject() != null) {
            dto.setProjectId(f.getMonthlyProgress().getProject().getProjectId());
            dto.setMonthName(f.getMonthlyProgress().getMonthName());
        }
        dto.setBestCaseOverrun(f.getBestCaseOverrun());
        dto.setExpectedOverrun(f.getExpectedOverrun());
        dto.setWorstCaseOverrun(f.getWorstCaseOverrun());
        dto.setShockBuffer(f.getShockBuffer());
        dto.setPredictedAt(f.getPredictedAt());
        return dto;
    }

    private InterventionDTO convertInterventionToDTO(Intervention i) {
        if (i == null) return null;
        InterventionDTO dto = new InterventionDTO();
        dto.setId(i.getId());
        if (i.getMonthlyProgress() != null && i.getMonthlyProgress().getProject() != null) {
            dto.setProjectId(i.getMonthlyProgress().getProject().getProjectId());
            dto.setMonthName(i.getMonthlyProgress().getMonthName());
        }
        dto.setInterventionCode(i.getInterventionCode());
        dto.setRecommendation(i.getRecommendation());
        dto.setPriority(i.getPriority());
        dto.setStatus(i.getStatus());
        dto.setCreatedAt(i.getCreatedAt());
        return dto;
    }

    private MonthlyProgressDTO convertProgressToDTO(MonthlyProgress m) {
        if (m == null) return null;
        MonthlyProgressDTO dto = new MonthlyProgressDTO();
        dto.setProjectId(m.getProject() != null ? m.getProject().getProjectId() : null);
        dto.setMonthName(m.getMonthName());
        dto.setRevisedCost(m.getRevisedCost());
        dto.setCumulativeExpenditure(m.getCumulativeExpenditure());
        dto.setPhysicalProgress(m.getPhysicalProgress());
        dto.setProgressVelocity(m.getProgressVelocity());
        dto.setFinancialBurnPct(m.getFinancialBurnPct());
        dto.setProgressFinancialDivergence(m.getProgressFinancialDivergence());
        dto.setIsStagnant(m.getIsStagnant());
        dto.setCostOverrunValue(m.getCostOverrunValue());
        dto.setCostOverrunFlag(m.getCostOverrunFlag());
        dto.setCompletionStatus(m.getCompletionStatus());
        return dto;
    }
}
