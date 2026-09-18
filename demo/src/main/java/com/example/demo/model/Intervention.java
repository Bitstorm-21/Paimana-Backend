package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "interventions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_intervention_monthly_progress",
                        columnNames = {"monthly_progress_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "intervention_id")
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

    @Column(name = "intervention_code", length = 50)
    private String interventionCode;

    @Column(name = "recommendation", columnDefinition = "TEXT")
    private String recommendation;

    @Column(name = "priority", length = 50)
    private String priority;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "PENDING";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}