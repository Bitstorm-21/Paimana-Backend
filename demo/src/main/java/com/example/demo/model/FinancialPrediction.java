package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "financial_predictions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_financial_monthly_progress",
                        columnNames = {"monthly_progress_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinancialPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "financial_prediction_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "monthly_progress_id",
            referencedColumnName = "id",
            nullable = false
    )
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MonthlyProgress monthlyProgress;

    @Column(name = "best_case_overrun", precision = 15, scale = 2)
    private BigDecimal bestCaseOverrun;

    @Column(name = "expected_overrun", precision = 15, scale = 2)
    private BigDecimal expectedOverrun;

    @Column(name = "worst_case_overrun", precision = 15, scale = 2)
    private BigDecimal worstCaseOverrun;

    @Column(name = "shock_buffer", precision = 15, scale = 2)
    private BigDecimal shockBuffer;

    @Column(name = "predicted_at")
    private LocalDateTime predictedAt;

    @PrePersist
    protected void onCreate() {
        if (predictedAt == null) {
            predictedAt = LocalDateTime.now();
        }
    }
}
