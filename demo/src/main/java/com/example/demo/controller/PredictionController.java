package com.example.demo.controller;

import com.example.demo.payload.FinancialPredictionDTO;
import com.example.demo.payload.InterventionDTO;
import com.example.demo.payload.MonthlyInsightResponse;
import com.example.demo.payload.RiskPredictionDTO;
import com.example.demo.security.services.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PredictionController {

    @Autowired
    private InsightService insightService;

    // =====================================================
    // 1. COMBINED MONTHLY INSIGHTS (PROGRESS + RISK + FINANCIAL + INTERVENTION)
    // =====================================================

    @GetMapping("/public/projects/{projectId}/months/{monthName}/insights")
    public ResponseEntity<MonthlyInsightResponse> getMonthlyInsights(
            @PathVariable Long projectId,
            @PathVariable String monthName) {

        MonthlyInsightResponse response = insightService.getMonthlyInsights(projectId, monthName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // =====================================================
    // 2. RISK PREDICTION BY PROJECT ID & MONTH NAME
    // =====================================================

    @GetMapping("/public/projects/{projectId}/months/{monthName}/risk")
    public ResponseEntity<RiskPredictionDTO> getRiskPrediction(
            @PathVariable Long projectId,
            @PathVariable String monthName) {

        RiskPredictionDTO response = insightService.getRiskPrediction(projectId, monthName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // =====================================================
    // 3. FINANCIAL PREDICTION BY PROJECT ID & MONTH NAME
    // =====================================================

    @GetMapping("/public/projects/{projectId}/months/{monthName}/financial")
    public ResponseEntity<FinancialPredictionDTO> getFinancialPrediction(
            @PathVariable Long projectId,
            @PathVariable String monthName) {

        FinancialPredictionDTO response = insightService.getFinancialPrediction(projectId, monthName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // =====================================================
    // 4. ACTIONABLE INTERVENTION BY PROJECT ID & MONTH NAME
    // =====================================================

    @GetMapping("/public/projects/{projectId}/months/{monthName}/intervention")
    public ResponseEntity<InterventionDTO> getIntervention(
            @PathVariable Long projectId,
            @PathVariable String monthName) {

        InterventionDTO response = insightService.getIntervention(projectId, monthName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // =====================================================
    // 5. COMBINED INSIGHTS BY PROJECT NAME & MONTH NAME
    // =====================================================

    @GetMapping("/public/projects/by-name/{projectName}/months/{monthName}/insights")
    public ResponseEntity<MonthlyInsightResponse> getMonthlyInsightsByProjectName(
            @PathVariable String projectName,
            @PathVariable String monthName) {

        MonthlyInsightResponse response = insightService.getMonthlyInsightsByProjectName(projectName, monthName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // =====================================================
    // 6. SEARCH PREDICTIONS VIA QUERY PARAMETERS
    // =====================================================

    @GetMapping("/public/predictions/search")
    public ResponseEntity<MonthlyInsightResponse> searchPredictions(
            @RequestParam(name = "projectId") Long projectId,
            @RequestParam(name = "monthName") String monthName) {

        MonthlyInsightResponse response = insightService.getMonthlyInsights(projectId, monthName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
