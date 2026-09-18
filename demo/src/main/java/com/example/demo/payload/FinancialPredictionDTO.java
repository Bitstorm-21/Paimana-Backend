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
public class FinancialPredictionDTO {

    private Long id;
    private Long projectId;
    private String monthName;
    private BigDecimal bestCaseOverrun;
    private BigDecimal expectedOverrun;
    private BigDecimal worstCaseOverrun;
    private BigDecimal shockBuffer;
    private LocalDateTime predictedAt;
}
