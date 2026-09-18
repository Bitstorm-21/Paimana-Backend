package com.example.demo.payload;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyProgressRequest {

    @NotBlank(message = "Month name cannot be blank")
    @JsonProperty("monthName")
    @JsonAlias({"MonthName", "month", "reportMonth"})
    private String monthName;

    @NotNull(message = "Revised cost cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cost cannot be negative")
    private BigDecimal revisedCost;

    @NotNull(message = "Cumulative expenditure cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Cumulative Expenditure cannot be negative")
    private BigDecimal cumulativeExpenditure;

    @NotNull(message = "Physical progress cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Physical progress cannot be negative")
    private BigDecimal physicalProgress;
}