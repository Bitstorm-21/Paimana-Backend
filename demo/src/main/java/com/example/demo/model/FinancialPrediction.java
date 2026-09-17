//package com.example.demo.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(
//        name = "financial_predictions",
//        uniqueConstraints = {
//                @UniqueConstraint(
//                        name = "uk_financial_project_month",
//                        columnNames = {"project_id", "month_name"}
//                )
//        }
//)
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class FinancialPrediction {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "financial_prediction_id")
//    private Long financialPredictionId;
//
//    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumns({
//            @JoinColumn(
//                    name = "project_id",
//                    referencedColumnName = "project_id",
//                    nullable = false
//            ),
//            @JoinColumn(
//                    name = "month_name",
//                    referencedColumnName = "month_name",
//                    nullable = false
//            )
//    })
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private MonthlyProgress projectMonthlyData;
//
//    @Column(name = "best_case_overrun")
//    private BigDecimal bestCaseOverrun;
//
//    @Column(name = "expected_overrun")
//    private BigDecimal expectedOverrun;
//
//    @Column(name = "worst_case_overrun")
//    private BigDecimal worstCaseOverrun;
//
//    @Column(name = "shock_buffer")
//    private BigDecimal shockBuffer;
//
//    @Column(name = "predicted_at")
//    private LocalDateTime predictedAt;
//
//    @PrePersist
//    protected void onCreate() {
//        predictedAt = LocalDateTime.now();
//    }
//}
