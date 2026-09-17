//package com.example.demo.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(
//        name = "interventions",
//        uniqueConstraints = {
//                @UniqueConstraint(
//                        name = "uk_intervention_project_month",
//                        columnNames = {"project_id", "month_name"}
//                )
//        }
//)
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class Intervention {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "intervention_id")
//    private Long interventionId;
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
//    @Column(name = "intervention_code")
//    private String interventionCode;
//
//    @Column(columnDefinition = "TEXT")
//    private String recommendation;
//
//    @Column(nullable = false)
//    private String status = "PENDING";
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//    }
//}