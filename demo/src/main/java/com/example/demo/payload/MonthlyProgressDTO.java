package com.example.demo.payload;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyProgressDTO {

    private Long projectId;

    @JsonProperty("monthName")
    @JsonAlias({"MonthName", "month", "reportMonth"})
    private String monthName;

    private BigDecimal revisedCost;

    private BigDecimal cumulativeExpenditure;

    private BigDecimal physicalProgress;

    private BigDecimal progressVelocity;

    private BigDecimal financialBurnPct;

    private BigDecimal progressFinancialDivergence;

    private Boolean isStagnant;

    private BigDecimal costOverrunValue;

    private Boolean costOverrunFlag;

    private Boolean completionStatus;
}
