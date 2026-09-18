package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "risk_predictions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_risk_monthly_progress",
                        columnNames = {"monthly_progress_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RiskPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "risk_prediction_id")
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

    @Column(name = "risk_score", precision = 10, scale = 2)
    private BigDecimal riskScore;

    @Column(name = "risk_level", length = 50)
    private String riskLevel;

    @Column(name = "shap_drivers", columnDefinition = "TEXT")
    private String shapDrivers;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "predicted_at")
    private LocalDateTime predictedAt;

    @PrePersist
    protected void onCreate() {
        if (predictedAt == null) {
            predictedAt = LocalDateTime.now();
        }
    }
}