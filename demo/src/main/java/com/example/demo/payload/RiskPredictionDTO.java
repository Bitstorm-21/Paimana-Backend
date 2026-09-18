package com.example.demo.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RiskPredictionDTO {

    private Long id;
    private Long projectId;
    private String monthName;
    private BigDecimal riskScore;
    private String riskLevel;
    private Object shapDrivers;
    private String modelName;
    private LocalDateTime predictedAt;
}
