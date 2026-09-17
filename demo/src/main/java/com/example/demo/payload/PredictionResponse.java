package com.example.demo.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponse {

    private BigDecimal riskScore;
    private String riskLevel;

    private BigDecimal bestCaseOverrun;
    private BigDecimal expectedOverrun;
    private BigDecimal worstCaseOverrun;

    private BigDecimal shockBuffer;

    private List<ShapDriver> shapDrivers;

    private String interventionCode;
    private String interventionRecommendation;
    private String interventionPriority;
}