package com.example.demo.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InterventionDTO {

    private Long id;
    private Long projectId;
    private String monthName;
    private String interventionCode;
    private String recommendation;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
}
