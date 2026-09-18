package com.example.demo.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyInsightResponse {

    private Long projectId;
    private String projectName;
    private String monthName;

    private MonthlyProgressDTO progress;
    private RiskPredictionDTO riskPrediction;
    private FinancialPredictionDTO financialPrediction;
    private InterventionDTO intervention;
}
