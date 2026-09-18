package com.example.demo.security.services;

import com.example.demo.payload.FinancialPredictionDTO;
import com.example.demo.payload.InterventionDTO;
import com.example.demo.payload.MonthlyInsightResponse;
import com.example.demo.payload.RiskPredictionDTO;

public interface InsightService {

    RiskPredictionDTO getRiskPrediction(Long projectId, String monthName);

    FinancialPredictionDTO getFinancialPrediction(Long projectId, String monthName);

    InterventionDTO getIntervention(Long projectId, String monthName);

    MonthlyInsightResponse getMonthlyInsights(Long projectId, String monthName);

    MonthlyInsightResponse getMonthlyInsightsByProjectName(String projectName, String monthName);
}
