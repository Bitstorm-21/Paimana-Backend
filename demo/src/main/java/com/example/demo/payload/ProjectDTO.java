package com.example.demo.payload;

import com.example.demo.model.Sector;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {
    private Long projectId;
    private String ProjectName ;
    private String projectDescription;
    private String state ;
    private Long OriginalCost ;
    private String startTime ;
    private String OriginalEndTime ;
}
