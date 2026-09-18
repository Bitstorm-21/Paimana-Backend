package com.example.demo.model;

import jakarta.persistence.*;
        import lombok.*;

        import java.math.BigDecimal;

@Entity
@Table(
        name = "project_monthly_data",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"project_id", "report_month"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // PROJECT
    // =========================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "project_id",
            nullable = false
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Project project;

    // =========================
    // USER PROVIDED DATA
    // =========================

    @Column(name = "revised_cost", nullable = false)
    private BigDecimal revisedCost;

    @Column(name = "cumulative_expenditure", nullable = false)
    private BigDecimal cumulativeExpenditure;

    @Column(name = "physical_progress", nullable = false)
    private BigDecimal physicalProgress;

    @Column(name = "report_month", nullable = false)
    private String monthName;

    // =========================
    // CALCULATED FEATURES
    // =========================

    @Column(name = "progress_velocity")
    private BigDecimal progressVelocity;

    @Column(name = "financial_burn_pct")
    private BigDecimal financialBurnPct;

    @Column(name = "progress_financial_divergence")
    private BigDecimal progressFinancialDivergence;

    @Column(name = "is_stagnant")
    private Boolean isStagnant;

    @Column(name = "cost_overrun_value")
    private BigDecimal costOverrunValue;

    @Column(name = "cost_overrun_flag")
    private Boolean costOverrunFlag;

    @Column(name = "completion_status")
    private Boolean completionStatus;

    // =========================
    // ML PREDICTION RESULTS
    // =========================

    @OneToOne(
            mappedBy = "monthlyProgress",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RiskPrediction riskPrediction;

    @OneToOne(
            mappedBy = "monthlyProgress",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FinancialPrediction financialPrediction;

    @OneToOne(
            mappedBy = "monthlyProgress",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Intervention intervention;
}



// velocity = 30 - previousProgress
//      │
//      ├── burn = 60 / 100 × 100
//      │
//      ├── divergence = burn - physicalProgress
//      │
//      ├── stagnant = velocity <= 0
//      │
//      ├── overrunValue = 120 - 100
//      │
//      └── overrunFlag = 120 > 100
//      │
//      ▼
//PostgreSQL for all the calculated value
