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
public class MLPredictionRequest {

    private Double physicalProgress;
    private Double financialBurnPct;
    private Double progressVelocity;
    private Double progressFinancialDivergence;
    private Integer isStagnant;
    private Double originalCost;
}
