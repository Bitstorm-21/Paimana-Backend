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
//        name = "risk_predictions",
//        uniqueConstraints = {
//                @UniqueConstraint(
//                        name = "uk_risk_project_month",
//                        columnNames = {"project_id", "month_name"}
//                )
//        }
//)
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class RiskPrediction {
//
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    @Column(name = "risk_prediction_id")
////    private Long id;
////
////    @OneToOne(fetch = FetchType.LAZY, optional = false)
////    @JoinColumns({
////            @JoinColumn(
////                    name = "project_id",
////                    referencedColumnName = "project_id",
////                    nullable = false
////            ),
////            @JoinColumn(
////                    name = "month_name",
////                    referencedColumnName = "month_name",
////                    nullable = false
////            )
////    })
////    @ToString.Exclude
////    @EqualsAndHashCode.Exclude
////    private MonthlyProgress projectMonthlyData;
////
////    @Column(name = "risk_score")
////    private BigDecimal riskScore;
////
////    @Column(name = "risk_level")
////    private String riskLevel;
////
////    @Column(name = "model_name")
////    private String modelName;
////
////    @Column(name = "model_version")
////    private String modelVersion;
////
////    @Column(name = "predicted_at")
////    private LocalDateTime predictedAt;
////
////    @PrePersist
////    protected void onCreate() {
////        predictedAt = LocalDateTime.now();
////    }
//}