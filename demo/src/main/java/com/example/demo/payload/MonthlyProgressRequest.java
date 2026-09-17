package com.example.demo.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyProgressRequest {
@Valid
@NotNull

    private String MonthName;
    @Valid
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "Cost can not be negative")
    private BigDecimal revisedCost;
    @Valid
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "Cumulative Expenditure cannot be negative")
    private BigDecimal cumulativeExpenditure;
    @Valid
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "Physical progress cannot be negative")
    private BigDecimal physicalProgress;
}