package com.example.demo.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MLPredictionResponse {

    private Double riskScore;
    private String riskLevel;

    private Double bestCaseOverrun;
    private Double expectedOverrun;
    private Double worstCaseOverrun;

    private Double shockBuffer;

    private List<Object> shapDrivers;

    private String interventionCode;
    private String interventionRecommendation;
    private String interventionPriority;
}
